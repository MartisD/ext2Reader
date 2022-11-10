 * java -jar ext2Reader.jar [imagename]
 * 
 * API example <br>
 * Volume vol = new Volume(String path) <br><br>
 * 
 * Ext2File ext2File = new Ext2File(Volume vol, String path)<br>
 * ext2File.read (long startByte, long length)<br>
 * ext2File.read (long length)<br>
 * ext2File.seek (long position)<br>
 * ext2File.position()<br>
 * ext2File.size()<br><br>
 * 
 * Directory dir = new Directory(Volume vol, String path)<br>
 * dir.getFileInfo()<br><br>
 * 
 * Helper.dumpHexBytes (byte[] bytes)<br><br>
 * 
 * Console console = new Console(Volume vol)<br><br>
 * 
 * @author Martynas Dabravalskis