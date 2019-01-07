import java.io.*;

public class WavReader {

	WavFile wavFile;
	int[] bufferArr;
	
	public WavReader(String file) {
		try {
			wavFile = WavFile.openWavFile(new File(file));
		}
		catch (Exception e) {
			System.out.println("Wav File could not be read");
			e.printStackTrace();
		}
		readWavFile();
	}
	
	private void readWavFile() {
		int channelCount = wavFile.getNumChannels();
		int frameCount = (int) (wavFile.getNumFrames());
		bufferArr = new int[channelCount * frameCount];
		try {
			int framesRead = wavFile.readFrames(bufferArr, frameCount);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WavFileException e) {
			e.printStackTrace();
		}
	}
	
	public void getDisplay() {
		wavFile.display();
	}
	
	public int[] wavArray() {
		return bufferArr;
	}
	
}
