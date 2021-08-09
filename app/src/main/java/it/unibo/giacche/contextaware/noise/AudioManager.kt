package it.unibo.giacche.contextaware.noise

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import it.unibo.giacche.contextaware.utils.Constants.AUDIO_BUFFER_SIZE
import it.unibo.giacche.contextaware.utils.Constants.AUDIO_DURATION
import it.unibo.giacche.contextaware.utils.Constants.AUDIO_SAMPLE_RATE
import kotlin.math.pow
import kotlin.math.sqrt

class AudioManager : CanReturnNoise {
    /**
     * Noise average is passed to callback function
     */
    override fun getNoise(callback: (Double) -> Unit) {
        val dispatcher: AudioDispatcher =
            AudioDispatcherFactory.fromDefaultMicrophone(AUDIO_SAMPLE_RATE, AUDIO_BUFFER_SIZE, 0)
        val audioProcessor = object : AudioProcessor {
            private var noises = 0.0
            private var times = 0

            override fun process(e: AudioEvent?): Boolean {
                if (e != null) {
                    val rms = AudioEvent.calculateRMS(e.floatBuffer)
                    if (rms.isFinite()) {
                        noises += rms.pow(2)
                        times++

                        if (e.timeStamp > AUDIO_DURATION / 1_000) {
                            dispatcher.stop()
                            callback(sqrt(noises / times))
                        }
                    }
                }
                return true
            }

            override fun processingFinished() {}
        }
        dispatcher.addAudioProcessor(audioProcessor)
        Thread(dispatcher).start()
    }
}