package underclothes.game.flowers;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class DetectAudio extends Thread {
	private AudioRecord audioRecord;
	private int bufferSize;
	private static int SAMPLE_RATE_IN_HZ = 8000;
	public boolean isRun = false;

	public CanvasSurfaceView canvasSurfaceView = null;

	public DetectAudio(CanvasSurfaceView canvasSurfaceView) {
		super();
		this.canvasSurfaceView = canvasSurfaceView;
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
	}

	public void run() {
		super.run();
		audioRecord.startRecording();
		// 用于读取的 buffer
		byte[] buffer = new byte[bufferSize];
		isRun = true;
		int division = 10;
		int divisionLength = (buffer.length / division);
		float[] volume = new float[division];
		while (isRun) {
			int r = audioRecord.read(buffer, 0, bufferSize) / division;
			int v = 0;
			for (int i = 0; i < division; i++) {
				v = 0;
				for (int j = divisionLength * i; j < divisionLength * (i + 1); j++) {
					// 这里没有做运算的优化，为了更加清晰的展示代码
					v += buffer[j] * buffer[j];
				}
				// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
				// 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
				// Log.d("spl", String.valueOf(v / (float) r));

				volume[i] = (float) (v / divisionLength);
			}
			// 将 buffer 内容取出，进行平方和运算
			v = 0;
			for (int i = 0; i < division; i++) {
				v += volume[i];
			}
			int average = v / division;
			v = 0;
			for (int i = 0; i < division; i++) {
				v += (volume[i] - average) * (volume[i] - average);
			}
			double Variance = Math.sqrt(v / division);
			Log.d("标准差为：", String.valueOf(Variance));
			Log.d("均值为：", String.valueOf(average));
			if (Variance > 180 && average > 2780) {
				trigge();
			}
		}
		audioRecord.stop();
		isRun = false;
	}

	public void trigge() {
		// canvasSurfaceView.soundPool.play(canvasSurfaceView.loadId1, 0.2f,
		// 0.2f, 1, 0, 1f);
		canvasSurfaceView.triggeAnimation();
		// canvasSurfaceView.glApp.hideElement();
		// if (canvasSurfaceView.toNext == true) {
		// canvasSurfaceView.longshake();
		// }

	}

	public void pause() {
		// 在调用本线程的 Activity 的 onPause 里调用，以便 Activity 暂停时释放麦克风
		isRun = false;
	}

	public void start() {
		// 在调用本线程的 Activity 的 onResume 里调用，以便 Activity 恢复后继续获取麦克风输入音量
		if (!isRun) {
			super.start();
		}
	}
}