export function AudioWave(
    audioPlayer: { value: HTMLMediaElement; },
    audioContext: AudioContext|undefined,
    frequency:Array<number>|null|undefined,
    waveArray?:Array<number>
    ) {
  if (!audioContext) {
    console.error('audioContext not create')
    return
  }
  let showNum: number;
  let source: MediaElementAudioSourceNode;
  let analyser: AnalyserNode;
  const Pause = () => {
    frequency?.fill(0);
    waveArray?.fill(16);
    source.disconnect()
    analyser.disconnect()
    clearInterval(showNum);
  };
  audioPlayer.value.onplay = () => {
    console.log('???')
    !audioContext && (audioContext = new AudioContext());
    source = audioContext.createMediaElementSource(audioPlayer.value)
    analyser = audioContext.createAnalyser()
    source.connect(analyser);
    // analyser.connect(audioContext.destination);
    analyser.fftSize = 32;
    const floatArray = new Uint8Array(32);
    // const dataArray = new Uint8Array(analyser.fftSize);
    const dataArray = new Uint8Array(16);
    analyser.getByteTimeDomainData(dataArray);
    analyser.connect(audioContext.destination);
    showNum = setInterval(() => {
      if (frequency) {
        analyser.getByteFrequencyData(dataArray);
        for (let i = 0; i < 16; i++) {
          frequency[i] = dataArray[i] / 16;
        }
        console.log(frequency)
      }
      if (waveArray) {
        analyser.getByteTimeDomainData(floatArray);
        floatArray.forEach((v, i) => {
          waveArray[i] = v / 8;
        });
      }
      if (audioPlayer.value.paused) {
        Pause();
      }
    }, 100);
  };
  audioPlayer.value.onpause = Pause;
  audioPlayer.value.onended = Pause;
  audioPlayer.value.onwaiting = Pause;

}