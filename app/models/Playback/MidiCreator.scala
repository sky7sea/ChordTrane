package models

import java.io.File
import java.io.IOException

import javax.sound.midi.Sequence
import javax.sound.midi.MidiEvent
import javax.sound.midi.MidiMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Track
import javax.sound.midi.InvalidMidiDataException

object MidiCreator{

	val DivisionType = Sequence.PPQ
	val Resolution = 24 //6 ticks per quarter note
	val NumberOfTracks = 1
	val FileType = 1

	//val sequence = new Sequence(DivisionType, Resolution)

	val QUARTER = Resolution
	val HALF = Resolution * 2
	val WHOLE = Resolution * 4
	val EIGHTH = Resolution / 2
	val TRIPLET = Resolution / 3
	val SIXTEENTH = Resolution / 4

	val Velocity = 60


	def noteEvent(command: Int, channel: Int, key: Int, velocity: Int, tick: Long) = {
		val message = new ShortMessage()
		message.setMessage(command, channel, key, velocity)
		new MidiEvent(message, tick)
	}

	def noteOn(key: Int, tick: Int) = {
		noteEvent(ShortMessage.NOTE_ON, 0, key, Velocity, tick)
	}

	def noteOff(key: Int, tick: Int) = {
		noteEvent(ShortMessage.NOTE_OFF, 0, key, Velocity, tick)
	}

	def noteOnOff(key: Int, startingTick: Int, duration: Int) = {
		Array(noteOn(key, startingTick), noteOff(key, startingTick + duration))
	}

	def programChange(patch: Int, channel: Int, tick: Int) = {
		val message = new ShortMessage()
		message.setMessage(ShortMessage.PROGRAM_CHANGE, channel, patch, -1)//2nd byte ignored
		new MidiEvent(message, tick)
	}

	def BassProgram(channel: Int) = programChange(33, channel, 0)
	def PianoProgram(channel: Int) = programChange(0, channel, 0)


	val sampleSequence = Array(
		Array(programChange(12, 0, 0)),
		noteOnOff(Note.getMidiNote("C", 6), 0, QUARTER),
		noteOnOff(Note.getMidiNote("D", 6), 24, QUARTER),
		noteOnOff(Note.getMidiNote("E", 6), 48, QUARTER),
		noteOnOff(Note.getMidiNote("F", 6), 72, QUARTER)
	).flatten




	def midiChordsToMidiEvent(midiChords: Array[MidiChord], subdivions: Int) = {
		midiChords.map{
			midiChord => {
				val midiTick = midiChord.tick * Resolution / subdivions
				midiChord.chord.map(noteOnOff(_, midiTick, EIGHTH)).flatten
			}
		}.flatten
	}
	def singleNotesToMidiEvent(notes: Array[SingleNote], subdivions: Int) = {
		notes.map{
			snote => {
				val midiTick = snote.tick * Resolution / subdivions
				noteOnOff(snote.note, midiTick, QUARTER)
			}
		}.flatten
	}

	// def test = {
	// 	val midiEvents = midiChordsToMidiEvent(PianoComper.testChords, 2)
	// 	val c = new MidiCreator
	// 	c.createTrack(midiEvents)
	// 	c.createMidi("C:/misty.midi")
	// }

}

class MidiCreator{
	val sequence = new Sequence(MidiCreator.DivisionType, MidiCreator.Resolution)

	def createTrack(midiEvents: Array[MidiEvent]){
		val track = sequence.createTrack()
		midiEvents.foreach{
			midiEvent => track.add(midiEvent)
		}
	}

	def createMidi(filePath: String){
		val outputFile = new File(filePath)
		MidiSystem.write(sequence, 1, outputFile)
	}
	





}