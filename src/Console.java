import java.util.Scanner;

/**
 * Class provides Console UI functionality.
 */
public class Console
{
    private final Volume vol;

    /**
     * Create Console
     * @param vol Volume
     */
    public Console(Volume vol)
    {
       this.vol = vol;
    }

    /**
     * Launches console
     */
    public void launch()
    {
        Directory directory = new Directory(vol,"/");  
        String input = "";
        Scanner in = new Scanner(System.in);

        System.out.println("Available commands: cd, ls, cat, quit");

        while(!input.equals(Constants.CONSOLE_QUIT))
        {
            Ext2File ext2File;

            System.out.print(directory.getCurrentPath() + " :");
            input = in.nextLine();

            if(input.equals(Constants.CONSOLE_LS))
                System.out.print(directory.getFileInfo());

            else if(input.length() > Constants.CONSOLE_CD.length()+1 && input.substring(0, Constants.CONSOLE_CD.length()).equals(Constants.CONSOLE_CD))
                directory.goToTarget(input.substring(Constants.CONSOLE_CD.length()+1));

            else if(input.length() > Constants.CONSOLE_CAT.length()+1 && input.substring(0, Constants.CONSOLE_CAT.length()).equals(Constants.CONSOLE_CAT))
            {
                String path = input.substring(Constants.CONSOLE_CAT.length()+1);
                boolean fullPath = false;

                for(int i = path.length()-1;i>=0;i--)
                {
                    if(path.charAt(i)== '/')
                    {
                        fullPath = true;
                        break;
                    }
                }

                if(fullPath==false)
                    path = directory.getCurrentPath() + path;

                ext2File = new Ext2File(vol, path);
                ext2File.print();
            }

            else if(!input.equals(Constants.CONSOLE_QUIT))
                System.out.println("Unknown or wrong command. Available commands: cd, ls, cat, quit");
        }
        in.close();
    }
}