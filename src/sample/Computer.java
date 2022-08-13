/*
 * Copyright (c) 71a1562385057d498290
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package sample;

import sample.bus.SpecBus;
import sample.cpu.Z80;
import sample.device.Clock;
import sample.utils.CircularBuffer;
import sample.utils.FileUtils;
import sample.utils.Maths;

import java.io.IOException;
import java.time.Duration;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;



/**
 * This class brings together multiple components
 * in order to build up a ZX Spectrum 48k machine.
 */
public class Computer extends JFrame {

    private static final int W = 256;
    private static final int H = 192;
    private static final String WINDOW_TITLE = "CIP-ZT 48K Emulator";

    private float scale = 3.0f;
    private int screenW = Math.round(W * scale);
    private int screenH = Math.round(H * scale);

    private int windowW = screenW + 3 * 48;
    private int windowH = screenH + 3 * 48;

    private ScreenComponent screenComponent;
    private BufferedImage screenBuffer;
    private KeyboardListener keyboardListener;
    private KempstonJoystickListener kempstonJoystickListener;
    private KempstonMouseListener kempstonMouseListener;

    private final ComputerWorker computer;



    public Computer() {
        screenBuffer = new BufferedImage(W, H,
                                         BufferedImage.TYPE_INT_RGB);
        screenComponent = new ScreenComponent();

        setTitle(WINDOW_TITLE);
        // not included with sample
    }



    /**
     * Create the main menu and return it.
     *
     * @return the newly created main menu
     */
    private JMenuBar createMenu() {
        JMenuBar mainMenu = new JMenuBar();
        mainMenu.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JMenu fileMenu = new JMenu("File");
        JMenu machineMenu = new JMenu("Machine");
        JMenu viewMenu = new JMenu("View");
        JMenu soundMenu = new JMenu("Sound");

        mainMenu.add(fileMenu);
        mainMenu.add(machineMenu);
        mainMenu.add(viewMenu);
        mainMenu.add(soundMenu);

        // not included with sample

        // set the default selection based on the scaling factor
        int a = 0;
        int i = viewMenu.getItemCount();

        while (a < i) {
            JMenuItem item = viewMenu.getItem(a);
            if (item.getText().contains(String.format("%.1f", scale))) {
                item.setIcon(selectedItem);
                break;
            }
            a++;
        }

        return mainMenu;
    }



    /**
     * Remove 'selected' icon from all menu items of a menu.
     *
     * @param menu the menu
     */
    private void removeMenuItemIcons(JMenu menu) {
        int a = menu.getItemCount();
        for (int x = 0; x < a; x++) {
            menu.getItem(x).setIcon(null);
        }
    }



    /**
     * Recompute the frame and screen panel dimensions.
     * Used after the scaling factor was changed.
     */
    private void updateDimensions() {
        // not included with sample
    }



    /**
     * Select the input files.
     * Display a file chooser for the user and record the user's selection
     */
    private String selectInputFile() {
        String fileName = null;
        JFileChooser fc = new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(FileUtils.snaFileFilter(false));
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Load SNA snapshot");
        fc.setApproveButtonText("Load");
        int result = fc.showDialog(this, null);

        if (result == JFileChooser.APPROVE_OPTION) {
            fileName = fc.getSelectedFile().getAbsolutePath();
        }
        return fileName;
    }



    /**
     * Set the border color
     *
     * @param color the new color
     */
    private void setBorderColor(int color) {
        getContentPane().setBackground(new Color(color));
    }



    /**
     * This is the screen panel onto which we draw the
     * actual generated image on every new frame.
     */
    private class ScreenComponent extends JComponent {

        private ScreenComponent() {
            setOpaque(true);

            // hide the cursor when inside the screen component
            /*
            setCursor(getToolkit().createCustomCursor(
                    new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0),
                    "ScreenComponent Cursor"));
            */
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(Computer.this.screenW,
                                 Computer.this.screenH);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Computer.this.screenW,
                                 Computer.this.screenH);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(Computer.this.screenW,
                                 Computer.this.screenH);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // not included with sample
        }
    }



    /**
     * The {@link ComputerWorker} thread, kind of glues together
     * all the different components and handles the main logic.
     */
    private class ComputerWorker extends SwingWorker<Void, Void> {

        // not included with sample

        /** Construct the {@link ComputerWorker}. */
        private ComputerWorker() {
            bus = new SpecBus();
            pixelHandler = new PixelHandler(bus);
            cpu = new Z80(bus);

            clock = new Clock();
            clock.addClockedDevice(cpu);

            // on real hardware at 3.5MHz, 1 frame would take 0.019968 seconds.
            // however, it shouldn't be a problem if we round that to 20ms and
            // even allow a 1ms margin later on.
            frameDuration = 20_000_000;
            // a complete frame takes 69888 clock cycles / t-states
            clockCyclesPerFrame = 69_888;

            audioBuffer = CircularBuffer.getInstance();
            // we take one averaged audio sample after every
            // 'audioSampleAtClockCycle' number of clock cycles.
            audioSampleAtClockCycle = 76;
        }



        @Override
        protected Void doInBackground() throws InterruptedException {

            // not included with sample

            // "every 16 frames, the ink and paper of all flashing bytes
            // is swapped; ie a normal to inverted to normal cycle takes
            // 32 frames"
            int flashinessFrameCounter = 0;

            // need this outside the main loop,
            // so we won't lose the partial audio sample at the end of
            // the current frame. instead it will be averaged into the
            // next frame's first sample.
            int audioCycles = 0;
            int audioSample = 0;

            byte[] audioSamples = new byte[clockCyclesPerFrame
                    / audioSampleAtClockCycle + 1];

            while (true) {  // the main 20ms loop, where all the magic happens.
                handleCurrentAction();

                if (paused) { Thread.sleep(20); continue; } // skip this frame

                // not included with sample

                while (tStates < clockCyclesPerFrame) {  // 69888 clock cycles
                    // not included with sample
                }

                // on the real hardware, as far as i know, the cpu interrupt is
                // generated at the end of the ~20ms frame / 69888 clock cycles.
                // in our case it doesn't really matter if we generate it
                // before or after the extra wait period.
                cpu.interrupt();
                // update the audio thread with new samples only if not muted
                if (!muted) {
                    updateAudioSamples(audioSamples);
                }

                // not included with sample
            }
        }



        /**
         * Create and start the audio thread.
         * Hint that the audio thread should have max priority.
         */
        private void startAudioThread() {
            // not included with sample
        }



        /**
         * Update the audio buffer with the current frame's samples
         * and notify the audio thread that there is new data to process.
         *
         * @param samples the byte array containing the current frame's audio samples
         */
        private void updateAudioSamples(byte[] samples) {
            // not included with sample
        }



        /**
         * Read keyboard input from the keyboard listeners
         * and make it available to be read by the cpu using the IN instruction.
         */
        private void handleInputPeripherals() {
              // not included with sample
        }



        /**
         * Update the screen by pushing into the image buffer
         * the decoded pixels of the current frame.
         *
         * @param frameNumber the frame number as counted by
         * the 'flashiness frame counter'
         */
        private void updateScreen(int frameNumber) {
            // not included with sample
        }



        /** Reset the currently running machine */
        private void resetMachine() {
            cpu.reset();
            bus.getMemoryDevice().reset();

            // not included with sample
        }



        /**
         * Load a SNA snapshot from a file.
         * The SNA format is described here:
         * https://sinclair.wiki.zxnet.co.uk/wiki/SNA_format
         *
         * Note that the Xs in method names are just for padding/symmetry purposes.
         * Methods dealing with main registers are prefixed with it, while methods
         * dealing with the alternate registers are suffixed.
         */
        private void loadSNASnapshot() {
            if (file == null) {
                System.out.println("Invalid snapshot file provided!");
                return;
            }

            byte[] snapshot = FileUtils.loadFile(file);
            if (snapshot.length == 0) {
                System.out.println("Snapshot has no content!");
                return;
            }
            // not included with sample
        }



        /**
         * Save a SNA snapshot to a file.
         * The SNA format is described here:
         * https://sinclair.wiki.zxnet.co.uk/wiki/SNA_format
         *
         * Note that the Xs in method names are just for padding/symmetry purposes.
         * Methods dealing with main registers are prefixed with it, while methods
         * dealing with the alternate registers are suffixed.
         */
        private void saveSNASnapshot() {
            if (file == null) {
                System.out.println("Invalid snapshot file provided!");
                return;
            }

            int[] snapshot = new int[49179]; // 48k

            // not included with sample
        }
    }
}
