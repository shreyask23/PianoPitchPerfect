import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.*;

public class Audience {
	
	static WavReader noisyCWav;
	static WavReader quietCWav;
	static WavReader constantCWav;
	static WavReader constantCNoisyWav;
	static WavReader LowCWav;
	static WavReader LowBWav;
	static int[] noisyCData;
	static int[] quietCData;
	static int[] constantCData;
	static int[] constantCNoisyData;
	static int[] lowCData;
	static int[] lowBData;
	static final int sampleSize = 2048;
	static final int sampleRate = 44100;
	static final double overlap = 0.25;
	static final double targetFreq = 130.8;
	static final int noiseSampleCount = 1;
	static final int signalSampleCount = 5;
	
	public static void main(String[] args) {
		loadData();
		int[] signalChunk = new int[(int) (sampleSize * signalSampleCount - (signalSampleCount - 1) * (1 - overlap) * sampleSize)];
		System.arraycopy(lowCData, 0, signalChunk, 0, signalChunk.length);
		SignalManager sig = new SignalManager();
		sig.configSignalManager(signalChunk, sampleRate, targetFreq, sampleSize, signalSampleCount, overlap);
		sig.processBuffer("Signal");
	}

	private static void loadData() {
		noisyCWav = new WavReader("resources/MiddleCHighNoise.wav");
		quietCWav = new WavReader("resources/MiddleCLowNoise.wav");
		constantCWav = new WavReader("resources/MiddleCConstant.wav");
		constantCNoisyWav = new WavReader("resources/MiddleCNoisyConstant.wav");
		LowCWav = new WavReader("resources/LowC.wav");
		LowBWav = new WavReader("resources/LowB.wav");
		noisyCData = noisyCWav.wavArray();
		quietCData = quietCWav.wavArray();
		constantCData = constantCWav.wavArray();
		constantCNoisyData = constantCNoisyWav.wavArray();
		lowCData = LowCWav.wavArray();
		lowBData = LowBWav.wavArray();
	}
}

/* WavFile.java and WavFileException.java classes are both courtesy
 * of Dr. Andrew Greenstead and can be found at http://www.labbookpages.co.uk/audio/javaWavFiles.html.
 */
