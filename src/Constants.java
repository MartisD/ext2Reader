public class Constants
{
    // Inode file modes
    public final static int IFDIR = 0x4000;      // Directory
    public final static int IFREG = 0x8000;      // Regular File
    public final static int IRUSR = 0x0100;      // User read
    public final static int IWUSR = 0x0080;      // User write
    public final static int IXUSR = 0x0040;      // User execute

    public final static int IRGRP = 0x0020;      // Group read
    public final static int IWGRP = 0x0010;      // Group write
    public final static int IXGRP = 0x0008;      // Group execute

    public final static int IROTH = 0x0004;      // Others read
    public final static int IWOTH = 0x0002;      // Others wite
    public final static int IXOTH = 0x0001;      // Others execute


    // General constants
    public final static int BLOCK_SIZE = 1024;
    public final static int INT_SIZE = 2147483647;
    public final static int INT_SIZE_BITS = 32;
    public final static int SECOND = 1000; // 1000ms = 1 second

    // Superblock offsets
    public final static int SUPERBLOCK_MAGIC_NUMBER = 0xef53;
    public final static int SUPERBLOCK_LABEL_SIZE = 16;
    public final static int SUPERBLOCK_MAGIC_NUMBER_OFFSET = 56;
    public final static int SUPERBLOCK_LABEL_OFFSET = 120;
    public final static int SUPERBLOCK_INODE_SIZE_OFFSET = 88;
    public final static int SUPERBLOCK_BLOCKS_PER_GROUP_OFFSET = 32;
    public final static int SUPERBLOCK_INODES_PER_GROUP_OFFSET = 40;
    public final static int SUPERBLOCK_OFFSET = 1024;
    
    // Group Descriptor constants
    public final static int GROUP_DESCRIPTOR_OFFSET = 2048;
    public final static int GROUP_DESCRIPTOR_INODE_TABLE_OFFSET = 8;
    public final static int GROUP_DESCRIPTOR_SIZE = 32;

    // Console commands
    public final static String CONSOLE_QUIT = "quit";
    public final static String CONSOLE_CD = "cd";
    public final static String CONSOLE_LS = "ls";
    public final static String CONSOLE_CAT = "cat";

    // Helper constants
    public final static int HELPER_HEX_PER_LINE = 16;
    public final static int HELPER_HEX_PER_HALF_LINE = 8;
    public final static int HELPER_ASCII_MIN_NUM = 32;
    public final static int HELPER_ASCII_MAX_NUM = 126;

    // Inode constants
    public final static int INODE_DIRECT_POINTERS = 12;
    public final static int INODE_DIRECT_POINTERS_OFFSET = 40;
    public final static int INODE_FILE_SIZE_UPPER_OFFSET = 108;
    public final static int INODE_FILE_TYPE_FILE = 1;
    public final static int INODE_FILE_TYPE_DIRECTORY = 2;
    public final static int INODE_ROOT = 2;
    public final static int INODES_PER_BLOCK = 256;
    
    // Directory constants
    public final static int DIRECTORY_MIN_SIZE = 12;
}