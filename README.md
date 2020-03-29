# Bragi
## Description
Bragi is a modular sound synthesizer, inspired by a school project which was inspired by the famous analog synthesizer : Minimoog.
## Architecture
Bragi is made of modules that have inputs and outputs.
Outputs can be connected to zero, one or several outputs.
Inputs can be connected to zero or one output.
## How it works
At every tick, each module processes n samples, in 4 steps :
* Read input samples
* Read tuning
* Compute output samples
* Write output samples
## Available modules
* ADSR: envelope generator with Attack, Decay, Sustain and Release parameters
* LFO: Low Frequency Oscillator
* VCO: Voltage Controlled Oscillator
* VCA: Voltage Controlled Amplifier
* VCF: Voltage Controlled Filter (with low-pass or high-pass response)
* Keyboard: basic keyboard based on computer keyboard (from F3 to E6)
* Oscilloscope: basic oscilloscope
* SpectrumAnalyzer: basic spectrum analyzer
* Mp3FilePlayer: basic MP3 file player
* WavFilePlayer: basic WAV file player
* WhiteNoiseGenerator: white noise generator
* PinkNoiseGenerator: pink noise generator
* Theremin: some kind of theremin (with just pitch and volume, not the incredible timbre of the real instrument)
* Sampler: an experimentation to change the sample rate or sample size
* Ditherer: an experimentation with dithering
* Speaker: a module connected to your speakers
* Microphone: a module connected to your microphone
## UI
Most modules have a basic UI, in Swing.
Each module has its own window and connections are not visibles or editables.
## Examples
### MP3 file player connected to speaker
```java
var player = new Mp3FilePlayer(TEST_MP3_PATH);
var speaker = new Speaker();

player.getOutputs().get(0).connect(speaker.getInputs().get(0));
player.getOutputs().get(1).connect(speaker.getInputs().get(1));
```
### Keyboard connected to VCO and VCO connected to speaker
```java
var keyboard = new Keyboard();
var vco = new VCO();
var speaker = new Speaker();
var oscilloscope = new Oscilloscope();

keyboard.connectTo(vco);
vco.connectTo(oscilloscope);
speaker.connectFrom(vco, vco);
```