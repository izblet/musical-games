#!/bin/bash

# Path to the SoundFont file
SOUNDFONT="/home/izabela/EXTRACURRICULAR/Music/soundfonts&setups/SGM-V2.01/SGM-V2.01.sf2"

# Directory containing the MIDI files
MIDI_DIR="."
# Output directory for the WAV files
OUTPUT_DIR="./wav_files"

# Create the output directory if it does not exist
mkdir -p "$OUTPUT_DIR"

# Loop through all MIDI files in the directory
for midi_file in "$MIDI_DIR"/*.mid; do
    # Get the base name of the MIDI file (without path and extension)
    base_name=$(basename "$midi_file" .mid)
    # Define the output WAV file path
    output_wav="$OUTPUT_DIR/${base_name}.wav"
    # Convert MIDI to WAV using FluidSynth
    fluidsynth -ni "$SOUNDFONT" "$midi_file" -F "$output_wav"
done

echo "Conversion complete."

