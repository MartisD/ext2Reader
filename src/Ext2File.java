import java.nio.ByteBuffer;

/**
 * Class for retrieving storing file data.
 * Allows for data to be read from file in an appropriate format.
 */
public class Ext2File
{
    private ByteBuffer dataBlock;
    private final Inode inode;
    private final String targetPath;
    private final int trueSize;
    private FileInfo fileInfo;
    private long position = 0;

    /**
     * Gets and saves file data at specified path in volume
     * @param vol Volume
     * @param targetPath File path
     */
    public Ext2File(Volume vol, String targetPath)
    {
        this.targetPath = targetPath;
        Directory directory = new Directory(vol, getDirectoryPath());
        if(directory.getFileInode(getFileName())!=0)
        {
            inode = vol.getInode(directory.getFileInode(getFileName()));
            fileInfo = new FileInfo(inode, vol);
            trueSize = fileInfo.getTrueSize();
        }
        else
        {
            inode = null;
            fileInfo = null;
            trueSize = 0;
        }
    }

    /**
     * Gets path of the directory target file is in
     * @return Path of the directory target file is in
     */
    private String getDirectoryPath()
    {
        String directoryPath = targetPath;
        for(int i = directoryPath.length()-1;i>=0;i--)
        {
            if(directoryPath.charAt(i)== '/')
            {
                directoryPath = directoryPath.substring(0, i+1);
                break;
            }
        }
        return directoryPath;
    }

    /**
     * Gets target file name
     * @return Target file name
     */
    private String getFileName()
    {
        String fileName = null;
        for(int i = targetPath.length()-1;i>=0;i--)
        {
            if(targetPath.charAt(i)== '/')
            {
                fileName = targetPath.substring(i+1,targetPath.length());
                break;
            }
        }
        return fileName;
    }

    /**
     * Reads length amount of bytes from file starting at byte startByte
     * @param startByte The position in file to start reading from
     * @param length The amount of bytes to be read
     * @return byte[]
     */
    public byte[] read(long startByte, long length)
    {
        if(startByte<0 || startByte>size())
            throw new IllegalArgumentException("StartByte out of bounds");

        if(length>size()-startByte)
            length = size()-startByte;

        if(length>Constants.INT_SIZE)
            throw new IllegalArgumentException("Array cannot be larger than maximum int size");

        dataBlock = fileInfo.getBlock((int)(startByte/Constants.BLOCK_SIZE));
        dataBlock.position((int)(startByte-(fileInfo.getCurrentBlock()*1024)));

        byte[] arr = new byte[(int)length];

        for(int i = 0;i<length;i++)
        {
            if(!dataBlock.hasRemaining())
            {
                dataBlock = fileInfo.getNextBlock();
            }
            arr[i] = dataBlock.get();
        }
        
        return arr;
    }

    /**
     * Reads length amount of bytes from file starting at current position
     * @param length The amount of bytes to be read
     * @return byte[]
     */
    public byte[] read(long length)
    {
        if(length>size()-position)
            length = size()-position;

        if(length>Constants.INT_SIZE)
            throw new IllegalArgumentException("Array cannot be larger than maximum int size");

        dataBlock = fileInfo.getBlock((int)(position/1024));
        dataBlock.position((int)(position-(fileInfo.getCurrentBlock()*1024)));

        byte[] arr = new byte[(int)length];

        for(int i = 0;i<length;i++)
        {
            if(!dataBlock.hasRemaining())
            {
                dataBlock = fileInfo.getNextBlock();
            }
            arr[i] = dataBlock.get();
        }
        
        return arr;
    }

    /**
     * Set current position in file to a new position
     * @param position New Position
     */
    public void seek(long position)
    {
        if(position<0 || position>size())
            throw new IllegalArgumentException("Position out of bounds");

        this.position = position;
    }

    /**
     * Gets current position in file
     * @return Current position in file
     */
    public long position()
    {
        return position;
    }

    /**
     * Gets file size
     * @return File size
     */
    public long size()
    {
        return inode.getSize();
    }

    /**
     * Print out the contents of the file
     */
    public void print()
    {
        if(inode!=null)
        {
            int length = trueSize;
            byte[] arr = new byte[1024];

            fileInfo.resetCurrentBlock();
            dataBlock = fileInfo.getNextUsefulBlock();

            for(int i = 0;i<length;i++)
            {
                if(i == 1024)
                {
                    System.out.print(new String(arr).trim());
                    i-=1024;
                    length-=1024;
                    arr = new byte[1024];
                }

                if(!dataBlock.hasRemaining())
                    dataBlock = fileInfo.getNextBlock();

                byte b = dataBlock.get();
                arr[i] = b;

            }
            System.out.print(new String(arr).trim());
            System.out.println();
        }
    }
}