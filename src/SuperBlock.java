import java.nio.ByteBuffer;

/**
 * Class for retrieving and storing SuperBlock data.
 * Allows for data to be read from SuperBlock in an appropriate format.
 */
public class SuperBlock
{
    private final ByteBuffer superblock;
    private final int magic_number;
    private final long blocks;
    private final long blocks_per_group;
    private final int inodes;
    private final int inodes_per_group;
    private final int inodeSize;
    private final int groups;
    private final byte[] label = new byte[Constants.SUPERBLOCK_LABEL_SIZE];

    /**
     * Reads and saves SuperBlock values from Volume
     * @param volume Volume to read the SuperBlock from
     */
    public SuperBlock(Volume volume)
    {
        superblock = volume.getByteBuffer(Constants.SUPERBLOCK_OFFSET,Constants.BLOCK_SIZE);

        magic_number = Helper.getUShort(superblock.getShort(Constants.SUPERBLOCK_MAGIC_NUMBER_OFFSET));
        inodes = superblock.getInt();
        blocks = Helper.getUInt(superblock.getInt());
        blocks_per_group = Helper.getUInt(superblock.getInt(Constants.SUPERBLOCK_BLOCKS_PER_GROUP_OFFSET));
        inodes_per_group = superblock.getInt(Constants.SUPERBLOCK_INODES_PER_GROUP_OFFSET);
        inodeSize = superblock.getInt(Constants.SUPERBLOCK_INODE_SIZE_OFFSET);

        superblock.position(Constants.SUPERBLOCK_LABEL_OFFSET);
        for(int i = 0;i<Constants.SUPERBLOCK_LABEL_SIZE;i++)
        {
            label[i] = superblock.get();

        }

        groups = (int) (blocks / blocks_per_group) + 1;
    }

    /**
     * Gets number of Inodes in this Volume
     * @return Number of Inodes in this Volume
     */
    public int getInodes() 
    {
        return inodes;
    }

    /**
     * Gets number of blocks in this Volume
     * @return Number of blocks in this Volume
     */
    public long getBlocks() 
    {
        return blocks;
    }

    /**
     * Gets number of blocks per Block Group
     * @return Number of blocks per Block Group
     */
    public long getBlocks_per_group() 
    {
        return blocks_per_group;
    }

    /**
     * Gets number of inodes per Block Group
     * @return Number of inodes per Block Group
     */
    public int getInodes_per_group() 
    {
        return inodes_per_group;
    }

    /**
     * Gets inode size
     * @return Inode size
     */
    public int getInodeSize() 
    {
        return inodeSize;
    }

    /**
     * Gets volume Label (name)
     * @return Volume Label (name)
     */
    public byte[] getLabel() 
    {
        return label;
    }

    /**
     * Gets number of Block Groups in Volume
     * @return Number of Block Groups in Volume
     */
    public int getGroups()
    {
        return groups;
    }

    /**
     * Prints SuperBlock Info
     */
    public void printInfo()
    {
        System.out.println("---------------------------------------");
        System.out.println("SUPERBLOCK INFO");
        System.out.println("Magic number: " + magic_number);
        System.out.println("Inodes:" + inodes);
        System.out.println("Blocks: " + blocks);
        System.out.println("Inodes per group: " + inodes_per_group);
        System.out.println("Blocks per group: " + blocks_per_group);
        System.out.println("Inode size: " + inodeSize);
        System.out.println("Label: " + new String(label));
        System.out.println("---------------------------------------");
    }
}