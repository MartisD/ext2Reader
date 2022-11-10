import java.nio.charset.StandardCharsets;

public class Helper
{
    /**
     * Prints byte[] in hex format with printable ASCII codes by the side
     * @param bytes Byte array
     */
    public static void dumpHexBytes(byte[] bytes)
    {
        byte[] ascii = new byte[Constants.HELPER_HEX_PER_LINE];

        int i = 0;
        while(i < bytes.length)
        {
            for(int a = 0;a < Constants.HELPER_HEX_PER_HALF_LINE;a++)
            {
                if(i<bytes.length)
                {
                    ascii[a] = bytes[i]; 

                    if(ascii[a] < Constants.HELPER_ASCII_MIN_NUM || ascii[a] > Constants.HELPER_ASCII_MAX_NUM)
                        ascii[a] = '.';

                    System.out.print(String.format("%02X ", bytes[i]));
                }
                else
                    System.out.print("XX ");
                i++;
            }

            System.out.print("| ");

            for(int a = Constants.HELPER_HEX_PER_HALF_LINE; a < Constants.HELPER_HEX_PER_LINE;a++)
            {
                if(i < bytes.length)
                {
                    ascii[a] = bytes[i];

                    if(ascii[a] < Constants.HELPER_ASCII_MIN_NUM || ascii[a] > Constants.HELPER_ASCII_MAX_NUM)
                        ascii[a] = '.';

                    System.out.print(String.format("%02X ", bytes[i]));
                }
                else
                    System.out.print("XX ");
                i++;
            }

            System.out.print("| ");
            System.out.format ("%s | %s", new String(ascii,StandardCharsets.US_ASCII).substring(0,ascii.length/2), new String(ascii,StandardCharsets.US_ASCII).substring(ascii.length/2));
            System.out.println();
        }
        
    }

    /**
     * Converts int to unsigned long
     * @param number Number
     * @return Unsigned long
     */
    public static long getUInt(int number)
    {
        return 0xffffffffL & (long)number;
    }

    /**
     * Converts short to unsigned int
     * @param number Number
     * @return Unsigned int
     */
    public static int getUShort(short number)
    {
        return 0xffff & (int)number;
    }
}