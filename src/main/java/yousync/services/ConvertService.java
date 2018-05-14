package yousync.services;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ConvertService {
    private static final Logger LOG = LoggerFactory.getLogger(ConvertService.class);

    public void convertToMp3(List<File> input) {
        for (File file : input) {
            convertToMp3(file, null);
        }
    }

    public void convertToMp3(File input, File output) {
        try {
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(new Integer(320000));
            audio.setChannels(new Integer(2));
            audio.setSamplingRate(new Integer(44100));
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp3");
            attrs.setAudioAttributes(audio);
            Encoder encoder = new Encoder();
            try {
                encoder.encode(input, output, attrs);
            } catch (EncoderException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            LOG.error("Input or Output does not exist! Input path: {}, output path: {}", input, output);
        }
    }
}
