# Screenshots & demo

The images referenced by the top-level `README.md` live here. The committed files are
**placeholders** — replace them with real device captures (same filenames) and the README updates
itself.

Expected files:

| File | What to show | Suggested size |
| --- | --- | --- |
| `demo.gif` | ~8 s loop: torch on → screen-light colour change → SOS | ≤ 280 px wide, < 5 MB |
| `torch.png` | Flashlight screen, torch on, brightness dial | phone portrait |
| `screen-light.png` | Screen-light mode with the HSV colour picker | phone portrait |
| `sos.png` | SOS Morse mode active | phone portrait |
| `settings.png` | Settings screen | phone portrait |

## Capturing on a device or emulator

Screenshots:

```bash
adb exec-out screencap -p > screenshots/torch.png
```

A demo recording, converted to an optimised GIF:

```bash
# 1. Record on the device (stop with Ctrl-C), then pull it
adb shell screenrecord --time-limit 10 --bit-rate 8000000 /sdcard/demo.mp4
adb pull /sdcard/demo.mp4 /tmp/demo.mp4

# 2. Convert to a small, smooth GIF (ffmpeg + a generated palette)
ffmpeg -i /tmp/demo.mp4 -vf "fps=15,scale=280:-1:flags=lanczos,palettegen" /tmp/pal.png
ffmpeg -i /tmp/demo.mp4 -i /tmp/pal.png \
  -lavfi "fps=15,scale=280:-1:flags=lanczos[x];[x][1:v]paletteuse" \
  screenshots/demo.gif
```

If you prefer an inline **video** instead of a GIF: drag the `.mp4` straight into the README on
github.com (or into a PR/issue comment) — GitHub hosts it and renders an inline player. Committed
video files only render as download links, which is why the demo here is a GIF.
