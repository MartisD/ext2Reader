import java.nio.ByteBuffer;

/**
 * Class for retrieving and storing Group Descriptor data.
 * Allows for data to be read from Group Descriptors in an appropriate format.
 */
public class GroupDescriptor
{
    private final ByteBuffer groupDescriptor;
    private final int[] iTablePointer;
    private final int groups;

    /**
     * Find GroupDescriptors in file and save their data
     * @param vol Volumes
     */
    public GroupDescriptor(Volume vol)
    {
        groups = vol.getSuperBlock().getGroups();
        iTablePointer = new int[groups];
        groupDescriptor = vol.getByteBuffer(Constants.GROUP_DESCRIPTOR_OFFSET, groups*Constants.GROUP_DESCRIPTOR_SIZE);

        for(int i = 0;i<groups;i++)
        {
            iTablePointer[i] = groupDescriptor.getInt((i*32)+8);
        }
    }

    /**
     * Get Inode Table pointer in group
     * @param group Group number
     * @return Inode Table pointer
     */
    public int getiTablePointer(int group)
    {
        return  iTablePointer[group];
    }

    /**
     * Prints Group Descriptors Info
     */
    public void printInfo()
    {
        System.out.println("---------------------------------------");
        System.out.println("Group Descriptors info");
        System.out.println("Block groups in disk: " + groups);
        System.out.println("Inode Table Pointers:");
        for(int pointer : iTablePointer)
            System.out.println(pointer);
        System.out.println("---------------------------------------");
    }

}