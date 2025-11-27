package com.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;

public class RTSPWorker {
	private MainWindow main;
	
	 // ====== 저장(FFmpeg) 관련 ======
    private final File recordDir = new File("C:\\Users\\factoryview\\recording");
    private Process ffmpegProcess;
    private OutputStream ffmpegStdin;
    private volatile boolean saving = false;
    private String currentOutputPath;
    private String ffmpegPath = "C:\\Users\\factoryview\\Documents\\ffmpeg-2025-08-07-git-fa458c7243-full_build\\bin\\ffmpeg.exe"; // PATH에 없으면 절대경로로 바꿔
    
    // 파일명에서 윈도우 금지문자 제거
    private static String sanitize(String s) {
        return s == null ? "" : s.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
    
    // 저장 제어 
    public synchronized void startSaving(String rtspUrl, String udId, String udName) {
    	if (!new File(ffmpegPath).exists()) {
    	    System.out.println("[SAVE][ERR] ffmpeg not found: " + ffmpegPath);
    	    return;
    	}
    	System.out.println("저장폴더: " + recordDir.getAbsolutePath());
    	
        // 폴더 준비
        if (!recordDir.exists()) recordDir.mkdirs();
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String safeId = sanitize(udId); 
        String safeName = sanitize(udName);
        if (safeId.isEmpty()) safeId = "unknown";
        currentOutputPath = new File(recordDir, "recording_" + safeName + "_" + safeId + "_" + ts + ".mp4").getAbsolutePath();
        try {
//        	ProcessBuilder pb = new ProcessBuilder(
//        		    ffmpegPath,
//        		    "-y",
//        		    "-rtsp_transport", "udp",
//        		    "-i", rtspUrl,
//        		    "-c:v", "libx264",
//        		    "-preset", "veryfast",
//        		    "-crf", "23",
//        		    "-c:a", "aac",
//        		    "-b:a", "128k",
//        		    "-movflags", "+faststart",
//        		    currentOutputPath
//        		);
        	ProcessBuilder pb = new ProcessBuilder(
        		    ffmpegPath, "-y",
        		    // RTSP는 TCP가 오디오/타임스탬프 안정적
        		    "-rtsp_transport", "tcp",
        		    // 타임스탬프/분석 여유를 줘서 a/v 놓침 방지
        		    "-fflags", "+genpts",
        		    "-use_wallclock_as_timestamps", "1",
        		    "-probesize", "1M", "-analyzeduration", "1M",
        		    "-i", rtspUrl,
        		    // 트랙 명시 매핑
        		    "-map", "0:v:0", "-map", "0:a:0?",
        		    // ★ 비디오 인코딩: 첫 프레임을 키프레임으로, 짧은 GOP
        		    "-c:v", "libx264",
        		    "-preset", "veryfast",
        		    "-tune", "zerolatency",
        		    "-g", "50",              // 50프레임마다 키프레임(약 2초@25fps)
        		    "-keyint_min", "50",
        		    "-x264-params", "scenecut=0",
        		    "-force_key_frames", "0", // ★ 시작 프레임 강제 키프레임
        		    // 오디오는 AAC로 정규화
        		    "-c:a", "aac", "-b:a", "128k", "-ar", "48000", "-ac", "2",
        		    // 컨테이너/메타 정리
        		    "-movflags", "+faststart",
        		    "-start_at_zero",
        		    "-shortest",             // ★ a/v 길이 불일치로 꼬리 늘어짐 방지
        		    currentOutputPath
        		);
          
            pb.directory(recordDir);
            pb.redirectErrorStream(true);
            ffmpegProcess = pb.start();
            ffmpegStdin = ffmpegProcess.getOutputStream();
            saving = true;

            new Thread(() -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()))) {
                    while (br.readLine() != null) { /* ignore */ }

                } catch (IOException ignored) { }
            }, "ffmpeg-gobbler").start();

            System.out.println("[SAVE] start: " + currentOutputPath);

        } catch (IOException e) {
            saving = false;
            System.out.println("[SAVE][ERR] ffmpeg start failed: " + e.getMessage());
        }
    }

    /** ffmpeg 저장 정상 종료(q) */
    public synchronized void stopSaving() {
        if (!saving) return;
        try {
            if (ffmpegStdin != null) {
                ffmpegStdin.write('q'); // 정상 종료
                ffmpegStdin.flush();
                ffmpegStdin.close();
            }
            long until = System.currentTimeMillis() + 500;
//            long until = System.currentTimeMillis();
            while (ffmpegProcess != null && ffmpegProcess.isAlive() && System.currentTimeMillis() < until) {
                try { Thread.sleep(50); } catch (InterruptedException ignored) { }
            }
            if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
                ffmpegProcess.destroy();
            }
            System.out.println("[SAVE] stop");
        } catch (Exception ignored) {
        } finally {
            saving = false;
            ffmpegProcess = null;
            ffmpegStdin = null;
            currentOutputPath = null;
        }
    }

	public void startStreaming() {
		String[] arrStreamOptions = {
		        "--network-caching=200",           // 400 → 200
		        ":sout-mux-caching=100",           // mux 버퍼 축소
		        ":sout=#transcode{vcodec=copy,acodec=mp4a,ab=128,channels=2}:rtp{sdp=rtsp://:" + main.getRTSPPort() + "/}",
		        ":sout-keep"
		    };

		try {
	        if (!mediaPlayer.status().isPlaying()) {
	            mediaPlayer.submit(() -> {
	                mediaPlayer.audio().setVolume(200);
	                mediaPlayer.media().startPaused("dshow://", arrStreamOptions);
	                mediaPlayer.controls().play();
	            });
	        }
	    } catch (Exception e) {
	    	// 예외 발생 시 처리
	    }
	}

	public void stopStreaming() {
		try {
			if (mediaPlayer.status().isPlaying()) {
				mediaPlayer.controls().stop();
			}

			if (mediaPlayer != null) {
				mediaPlayer.release();
			}

			if (mediaPlayerFactory != null) {
				mediaPlayerFactory.release();
			}

		} catch (Exception e) {
		}
	}

	private MediaPlayerFactory mediaPlayerFactory;
	private MediaPlayer mediaPlayer;

	public RTSPWorker(MainWindow m) {
		this.main = m;
		mediaPlayerFactory = new MediaPlayerFactory();
		mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
	}
}
