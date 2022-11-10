import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Class for retrieving and storing Directory data.
 * Allows for data to be read from Directory in an appropriate format.
 */
public class Directory
{
    private final Volume vol;
    private ByteBuffer dataBlock;
    private Inode inode;
    private String targetPath;
    private String currentPath = "/";
    private String nextPath;
    private FileInfo fileInfo;
    private String previousPath;
    private boolean found = false;
    private ArrayList<DirectoryEntry> directoryEntries = new ArrayList<DirectoryEntry>();

    /**
     * Gets and saves directory data at specified path in volume
     * @param vol Volume
     * @param targetPath Directory path
     */
    public Directory(Volume vol, String targetPath)
    {
        this.vol = vol;
        this.targetPath = targetPath;

        nextPath = targetPath;

        inode = vol.getInode(Constants.INODE_ROOT);
        fileInfo = new FileInfo(inode, vol);
        dataBlock = fileInfo.getNextUsefulBlock();

        traverse();
    }

    /**
     * Traverses through directories to find the directory at specified path
     */
    private void traverse()
    {
        found = false;
        getDirectoryEntries();
        getNextPath();
        for(DirectoryEntry entry : directoryEntries)
        {
            if(entry.fileName.equals(nextPath) && (entry.fileType == Constants.INODE_FILE_TYPE_DIRECTORY || vol.getInode(entry.inode).isDir()))
            {
                found = true;

                inode = vol.getInode(entry.inode);
                fileInfo = new FileInfo(inode, vol);
                fileInfo.resetCurrentBlock();
                dataBlock = fileInfo.getNextUsefulBlock();

                if(nextPath.equals(".."))
                {
                    if(currentPath.length()>1)
                        currentPath = currentPath.substring(0, currentPath.length()-1);
                    for(int i = currentPath.length()-1;i>=0;i--)
                    {
                        if(currentPath.charAt(i) == '/')
                        {
                            currentPath = currentPath.substring(0, i+1);
                            break;
                        }
                    }
                    break;
                }
                if(nextPath.equals("."))
                {
                    found = true;
                    break;
                }
                currentPath += nextPath + "/";
                if(!currentPath.equals(targetPath)) 
                    traverse();
                break;
            }
        }
        fileInfo.resetCurrentBlock();
        dataBlock = fileInfo.getNextUsefulBlock();
    }

    /**
     * Saves all directory entries in an array
     */
    private void getDirectoryEntries()
    {
        directoryEntries.clear();
        int inode;
        int length;
        int nameLength;
        int fileType;
        int position = 0;
        
        dataBlock.position(position);

        while(fileInfo.getCurrentBlock() != fileInfo.getNumUsefulBlocks()-1 || dataBlock.remaining()>=12)
        {
            inode = dataBlock.getInt();
            length = dataBlock.getChar();
            nameLength = dataBlock.get();
            fileType = dataBlock.get();
            byte[] fileName = new byte[nameLength];
            for(int i = 0;i<nameLength;i++)
            {
                fileName[i] = dataBlock.get();
            }
            if(nameLength!=0 && inode != 0 && nameLength != 0)
                directoryEntries.add(new DirectoryEntry(inode, length, nameLength, fileType, fileName));
            position = position+length;
            dataBlock.position(position);
            if(dataBlock.remaining()<=Constants.DIRECTORY_MIN_SIZE && this.fileInfo.getCurrentBlock() != fileInfo.getNumUsefulBlocks()-1)
            {
                dataBlock = fileInfo.getNextUsefulBlock();
                position = 0;
                dataBlock.position(0);
            }
        }
    }

    /**
     * Gets the path of the next directory to go to when traversing
     */
    private void getNextPath()
    {
        if(targetPath.length()>=currentPath.length())
            nextPath = targetPath.substring(currentPath.length());
        for(int i = 0;i<nextPath.length();i++)
        {
            if(nextPath.charAt(i)== '/')
            {
                nextPath = nextPath.substring(0, i);
                break;
            }
        }
    }
    
    /**
     * Gets the inode number of file
     * @param file File name
     * @return Inode number of file
     */
    public int getFileInode(String file)
    {
        getDirectoryEntries();
        for(DirectoryEntry entry : directoryEntries)
        {
            if((entry.fileName.equals(file)) && (entry.fileType == Constants.INODE_FILE_TYPE_FILE || vol.getInode(entry.inode).isDir()==false))
            {
                fileInfo.resetCurrentBlock();
                dataBlock = fileInfo.getNextUsefulBlock();
                return entry.inode;
            }
        }
        System.out.println("Failed to find file");

        fileInfo.resetCurrentBlock();
        dataBlock = fileInfo.getNextUsefulBlock();

        return 0;
    }

    /**
     * Gets string with the contents of directory in UNIX style
     * @return String with the contents of directory in UNIX style
     */
    public String getFileInfo()
    {
        String fileInfo = "";
        long largestSize = 0;
        getDirectoryEntries();

        for(DirectoryEntry entry : directoryEntries)
            if(vol.getInode(entry.inode).getSize()>largestSize)
                largestSize = vol.getInode(entry.inode).getSize();

        for(DirectoryEntry entry : directoryEntries)
        {
            fileInfo = fileInfo 
            + vol.getInode(entry.inode).getFileModeString() + " "
            + String.format("%1$2d", vol.getInode(entry.inode).gethardLinks()) + " " 
            + String.format("%1$4d", vol.getInode(entry.inode).getOwnerId()) + " " 
            + String.format("%1$4d", vol.getInode(entry.inode).getGroupId()) + " " 
            + String.format("%1$"+ String.valueOf(largestSize).length() + "d", vol.getInode(entry.inode).getSize()) + " "
            + vol.getInode(entry.inode).getLastModifiedDate() + 
            " " + entry.fileName + "\n";
        }
        return fileInfo;
    }

    /**
     * Goes to specified directory
     * @param target Directory path
     */
    public void goToTarget(String target)
    {
        if(target.charAt(target.length()-1) != '/')
            target+= "/";
        
        previousPath = currentPath;

        // if first char in path String is '/', start from root directory
        if(target.charAt(0) == '/')
        {
            currentPath = "/";
            targetPath = target;
            nextPath = targetPath;

            inode = vol.getInode(Constants.INODE_ROOT);
            fileInfo = new FileInfo(inode, vol);
            dataBlock = fileInfo.getNextUsefulBlock();

            if(!target.equals("/"))
                traverse();
        }
        else
        {
            targetPath = currentPath + target;
            nextPath = targetPath;
            
            traverse();
        }

        // if could not find file, return to starting directory
        if(found==false && currentPath != targetPath)
        {
            currentPath = "/";
            targetPath = previousPath;

            inode = vol.getInode(Constants.INODE_ROOT);
            fileInfo = new FileInfo(inode, vol);
            dataBlock = fileInfo.getNextUsefulBlock();

            System.out.println("Failed to find directory");

            traverse();
        }
    }

    /**
     * Gets current directory path
     * @return Current directory path
     */
    public String getCurrentPath()
    {
        return currentPath;
    }

    /**
     * Inner class to save Directory Entries
     * No getters/setters needed, since this is a private Inner class 
     */
    private class DirectoryEntry
    {
        final private int inode;
        final private int length;
        final private int nameLength;
        final private int fileType;
        final private String fileName;

        /**
         * Create a Directory Entry
         * @param inode File inode
         * @param length Entry length
         * @param nameLength File name length
         * @param fileType File type
         * @param filename File name
         */
        private DirectoryEntry(int inode, int length, int nameLength, int fileType, byte[] filename)
        {
            this.inode = inode;
            this.length = length;
            this.nameLength = nameLength;
            this.fileType = fileType;
            this.fileName = new String(filename);
        }
    }

}