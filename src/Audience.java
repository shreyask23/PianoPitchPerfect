import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.math.*;

public class Audience {
	
	static WavReader noisyCWav;
	static WavReader quietCWav;
	static WavReader constantCWav;
	static WavReader constantCNoisyWav;
	static int[] noisyCData;
	static int[] quietCData;
	static int[] constantCData;
	static int[] constantCNoisyData;
	static final int sampleSize = 8192;
	
	public static void main(String[] args) {
		//Currently testing FFT efficiency and accuracy
		loadData();
		constantCNoisyWav.getDisplay();
		System.out.println("---");
		int[] CChunk = new int[sampleSize];
		System.arraycopy(constantCNoisyData, 0, CChunk, 0, sampleSize);
		long startTime = System.nanoTime();
		FFT tester = new FFT(CChunk);
		ComplexNumber[] fftout = tester.fft(tester.getTData());
		long endTime = System.nanoTime();
		double[] magnitudes = tester.freqToMagnitude(fftout);
		ArrayList<Integer> peaks = tester.findLocalPeaks(magnitudes);
		double CAmplitude = tester.checkFrequencyPeak(magnitudes, peaks, 261.6, 44100);
		int maxIndex = tester.maxPeakQuick(magnitudes, peaks);
		System.out.println("FFT evaluation time: " + new Double((endTime - startTime) / 1000000).toString() + " milliseconds.");
		System.out.println("C frequency magnitude: " + new Double(CAmplitude).toString());
		System.out.println("FFT maximum magnitude: " + new Double(magnitudes[maxIndex]).toString());
		System.out.println("Maximum magnitude frequency: " + new Double(maxIndex * 44100 / magnitudes.length).toString());
		System.out.println("FFT bin 1 below: " + new Double(magnitudes[tester.approxFreqBin(magnitudes, 44100, 261.6)]).toString());
		System.out.println("FFT bin 1 above magnitude: " + new Double(magnitudes[tester.approxFreqBin(magnitudes, 44100, 261.6) + 1]));
	}

	private static void loadData() {
		noisyCWav = new WavReader("resources/MiddleCHighNoise.wav");
		quietCWav = new WavReader("resources/MiddleCLowNoise.wav");
		constantCWav = new WavReader("resources/MiddleCConstant.wav");
		constantCNoisyWav = new WavReader("resources/MiddleCNoisyConstant.wav");
		noisyCData = noisyCWav.wavArray();
		quietCData = quietCWav.wavArray();
		constantCData = constantCWav.wavArray();
		constantCNoisyData = constantCNoisyWav.wavArray();
	}

}

/* WavFile.java and WavFileException.java classes are both courtesy
 * of Dr. Andrew Greenstead and can be found at http://www.labbookpages.co.uk/audio/javaWavFiles.html.
 */
