import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for retrieving and storing Inode data.
 * Allows for data to be read from Inode in an appropriate format
 */
public class Inode
{
    private final int fileMode;

    private final int ownerId;
    private final int groupId;

    private final long accessTime;
    private final long lastModifiedTime;
    private final long creationTime;
    private final long deletedTime;

    private final int hardLinks;

    private final long[] blockPointer = new long[Constants.INODE_DIRECT_POINTERS];
    private final long indirectPointer;
    private final long doubleIndirectPoint;
    private final long tripleIndirectPointer;

    private final int fileSizeLower;
    private final int fileSizeUpper;

    private final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd HH:mm");

    /**
     * Gets and saves Inode data
     * @param inode Inode ByteBufffer
     */
    public Inode(ByteBuffer inode)
    {
        fileMode = Helper.getUShort(inode.getShort());
        ownerId = Helper.getUShort(inode.getShort());
        fileSizeLower = inode.getInt();

        accessTime = Helper.getUInt(inode.getInt())* Constants.SECOND;
        creationTime = Helper.getUInt(inode.getInt())* Constants.SECOND;
        lastModifiedTime = Helper.getUInt(inode.getInt())* Constants.SECOND;
        deletedTime =  Helper.getUInt(inode.getInt())* Constants.SECOND;

        groupId = Helper.getUShort(inode.getShort());
        hardLinks = Helper.getUShort(inode.getShort());

        inode.position(Constants.INODE_DIRECT_POINTERS_OFFSET);
        for(int i = 0;i<Constants.INODE_DIRECT_POINTERS;i++)
        {
            blockPointer[i] = Helper.getUInt(inode.getInt());
        }

        indirectPointer = Helper.getUInt(inode.getInt());
        doubleIndirectPoint = Helper.getUInt(inode.getInt());
        tripleIndirectPointer = Helper.getUInt(inode.getInt());

        inode.position(Constants.INODE_FILE_SIZE_UPPER_OFFSET);
        fileSizeUpper = inode.getInt();
    }

    /**
     * Gets last modified date
     * @return Last modified date
     */
    public String getLastModifiedDate()
    {
        return formatter.format(new Date(lastModifiedTime));
    }

    /**
     * Gets array containing block pointers
     * @return Array containing Block Pointers
     */
    public long[] getBlockpointer()
    {
        return blockPointer;
    }

    /**
     * Gets indirect pointer
     * @return Indirect pointer
     */
    public long getIndirectPointer()
    {
        return indirectPointer;
    }

    /**
     * Gets double indirect pointer
     * @return Double ndirect ointer
     */
    public long getDoubleIndirectPointer()
    {
        return doubleIndirectPoint;
    }

    /**
     * Gets triple indirect pointer
     * @return Triple indirect pointer
     */
    public long getTripleIndirectPointer()
    {
        return tripleIndirectPointer;
    }

    /**
     * Gets number of hard links
     * @return Number of hard links
     */
    public int gethardLinks()
    {
        return hardLinks;
    }

    /**
     * Gets group id
     * @return Group id
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * Gets owner id
     * @return Owner id
     */
    public int getOwnerId()
    {
        return ownerId;
    }

    /**
     * @return True if File is a Directory
     */
    public boolean isDir()
    {
        if((fileMode & Constants.IFDIR)!=0)
            return true;
        else
            return false;
    }

    /**
     * Gets file size
     * @return File Size
     */
    public long getSize()
    {
        return (((long) fileSizeUpper) << Constants.INT_SIZE_BITS) | (fileSizeLower & 0xffffffffL);
    }

    /**
     * Gets file mode in character form
     * @return File mode in character form
     */
    public String getFileModeString()
    {
        String dir = "-";
        String ru = "-";
        String wu = "-";
        String xu = "-";
        String rg = "-";
        String wg = "-";
        String xg = "-";
        String ro = "-";
        String wo = "-";
        String xo = "-";

        if((fileMode & Constants.IFDIR)!=0)
            dir = "d";
        if((fileMode & Constants.IRUSR) !=0 )
            ru = "r";
        if((fileMode & Constants.IWUSR) !=0 )
            wu = "w";
        if((fileMode & Constants.IXUSR) !=0 )
            xu = "x";
        if((fileMode & Constants.IRGRP) !=0 )
            rg = "r";
        if((fileMode & Constants.IWGRP) !=0 )
            wg = "w";
        if((fileMode & Constants.IXGRP) !=0 )
            xg = "x";
        if((fileMode & Constants.IROTH) !=0 )
            ro = "r";
        if((fileMode & Constants.IWOTH) !=0 )
            wo = "w";
        if((fileMode & Constants.IXOTH) !=0 )
            xo = "x";

        return dir + ru + wu + xu + rg + wg + xg + ro + wo + xo;
    }
}
