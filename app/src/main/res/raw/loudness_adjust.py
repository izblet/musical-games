import os
import shutil
from pydub import AudioSegment

def double_volume(input_dir, output_dir, backup_dir):
    # Create output and backup directories if they do not exist
    os.makedirs(output_dir, exist_ok=True)
    os.makedirs(backup_dir, exist_ok=True)

    for filename in os.listdir(input_dir):
        if filename.endswith(".wav"):
            # Construct full file paths
            input_path = os.path.join(input_dir, filename)
            output_path = os.path.join(output_dir, filename)
            backup_path = os.path.join(backup_dir, filename)

            # Backup the original file
            shutil.copy(input_path, backup_path)

            # Load the audio file
            audio = AudioSegment.from_wav(input_path)

            # Increase the volume
            louder_audio = audio + 6  # Increase by 6 dB (approximately twice as loud)

            # Export the louder audio file
            louder_audio.export(output_path, format="wav")

            print(f"Processed {filename}, backup created, and louder version saved.")

# Define your directories
input_directory = "."
output_directory = "./out"
backup_directory = "./backup"

# Run the function
double_volume(input_directory, output_directory, backup_directory)
