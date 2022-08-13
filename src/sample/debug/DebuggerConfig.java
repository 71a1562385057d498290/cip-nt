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


package sample.debug;

import java.util.Objects;



/**
 * Holds CLI provided debugging and testing options.
 */
public class DebuggerConfig {

    /* To enable debug mode, pass the -Ddebug option through the CLI. */
    public static final boolean DBG_MODE = Objects.nonNull(System.getProperty("debug"));

    /* To enable step by step debug mode, pass the -Dstep option through the CLI. */
    public static final boolean DBG_SBS_MODE = Objects.nonNull(System.getProperty("step"));

    /*
     * To enable test mode, pass the -Dtest option through the CLI.
     * Test mode assumes that zexdoc is being run and it will dump the result to the console.
     */
    public static final boolean TST_MODE = Objects.nonNull(System.getProperty("test"));

    /*
     * Track unsupported instructions.
     * To enable this mode, pass the -Dtu option through the CLI.
     * This mode will keep track of all the unsupported opcodes encountered during execution.
     * Currently this feature is disabled since we implemented the entire instructions set,
     * both documented and undocumented.
     */
    /* public static final boolean TRACK_UNSUPPORTED = Objects.nonNull(System.getProperty("tu")); */
}
