import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Class allows for data to be read from disk.
 * Stores SuperBlock, GroupDescriptor and Inodes.
 */
public class Volume
{
    private final SuperBlock superBlock;
    private final GroupDescriptor groupDescriptor;
    private RandomAccessFile raf;
    private Inode[] inode;

    /**
     * Opens up the disk, reads the superblock, group descriptors and inodes.
     * @param name Name of the Volume file
     */
    public Volume(String name)
    {
        try
        {
            System.out.println("---------------------------------------");
            System.out.println("Opening disk " + name);

            raf = new RandomAccessFile(name, "r");

            System.out.println("Disk Opened");
            System.out.println("---------------------------------------");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        superBlock = new SuperBlock(this);
        groupDescriptor = new GroupDescriptor(this);
        findInodes();
    }

    /**
     * Finds all inodes in the volume
     */
    private void findInodes()
    {
        inode = new Inode[superBlock.getInodes_per_group()*superBlock.getGroups()+1];
        for(int i = 0;i<superBlock.getGroups();i++)
        {
            for(int a = 1;a<=superBlock.getInodes_per_group();a++)
            {
                int index = a + (i * superBlock.getInodes_per_group());
                int inodeOffset = (groupDescriptor.getiTablePointer(i) * Constants.BLOCK_SIZE) + (superBlock.getInodeSize() * (a - 1));
                inode[index] = new Inode(getByteBuffer(inodeOffset, superBlock.getInodeSize()));
            }
        }
    }

    /**
     * Reads length bytes from volume and returns ByteBuffer
     * @param offset The start offset in Volume at which the data is written
     * @param length The amount of bytes to be read
     * @return ByteBuffer
     */
    public ByteBuffer getByteBuffer(long offset,int length)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        byte[] bytes = new byte[length];
        try
        {
            raf.seek(offset);
            raf.read(bytes,0,length);
            byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }  
        return byteBuffer;
    }

    /**
     * Reads length bytes from volume and returns byte[]
     * @param offset The start offset in Volume at which the data is written
     * @param length The amount of bytes to be read
     * @return byte[]
     */
    public byte[] getBytes(long offset,int length)
    {
        byte[] bytes = new byte[length];
        try
        {
            raf.seek(offset);
            raf.read(bytes,0,length);
        }
        catch(IOException e)
        {
            System.out.println(e);
        }  
        return bytes;
    }

    /**
     * Wraps a byte array into a buffer and sets the order to Little Endian
     * @param bytes byte array
     * @return ByteBuffer from byte array
     */
    public ByteBuffer wrapBuffer(byte[] bytes)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer;
    }

    /**
     * Gets Superblock
     * @return SuperBlock
     */
    public SuperBlock getSuperBlock()
    {
        return superBlock;
    }

    /**
     * Gets Group Descriptor
     * @return GroupDescriptor
     */
    public GroupDescriptor getGroupDescriptor()
    {
        return groupDescriptor;
    }

    /**
     * Gets Inode at specified index from the array
     * @param index index of the Inode in the array
     * @return Inode at index from the array
     */
    public Inode getInode(int index)
    {
        return inode[index];
    }

}