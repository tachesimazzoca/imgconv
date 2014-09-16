package com.github.tachesimazzoca.imgconv.cli;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.github.tachesimazzoca.imgconv.ConvertOption;
import com.github.tachesimazzoca.imgconv.Geometry;
import com.github.tachesimazzoca.imgconv.ImageUtils;

public class ConvertCommand {
    private static final String OPT_STRIP = "strip";
    private static final String OPT_GEOMETRY = "geometry";
    private static final String REGEX_GEOMETRY = "([0-9]+)?x([0-9]+)?(<|>|!)?";

    public static void main(String[] args) throws IOException, ParseException {
        // command options
        Options opts = new Options();
        opts.addOption(OPT_STRIP, false, "Strip all profiles and comments");
        opts.addOption(OPT_GEOMETRY, true, REGEX_GEOMETRY);

        CommandLine cmd = new PosixParser().parse(opts, args);

        // args
        String[] files = cmd.getArgs();
        if (files.length == 0) {
            new HelpFormatter().printHelp("convert /path/to/src /path/to/dest", opts);
            return;
        }
        if (files.length != 2)
            throw new IllegalArgumentException("Invalid number of files");

        // build convert options
        ConvertOption.Builder co = ConvertOption.builder();
        // -strip
        if (cmd.hasOption(OPT_STRIP))
            co.flag(ConvertOption.Flag.STRIP);
        // -geometry
        if (cmd.hasOption(OPT_GEOMETRY))
            co.geometry(convertArgToGeometry(cmd.getOptionValue(OPT_GEOMETRY)));
        // format
        ConvertOption.Format sfmt = FormatOption.fromPath(files[0]);
        ConvertOption.Format dfmt = FormatOption.fromPath(files[1]);
        if (sfmt != dfmt) {
            co.format(dfmt);
        }

        // do convert
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            ImageUtils.convert(
                    new FileInputStream(new File(files[0])),
                    new FileOutputStream(new File(files[1])),
                    co.build());
        } catch (IOException e) {
            throw e;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

    private static void closeQuietly(Closeable o) {
        try {
            if (o != null)
                o.close();
        } catch (Exception e) {
        }
    }

    protected static Geometry convertArgToGeometry(String arg) {
        Pattern ptn = Pattern.compile(REGEX_GEOMETRY);
        Matcher m = ptn.matcher(arg);
        if (!m.matches())
            throw new IllegalArgumentException(
                    "Unable to parse the value of the -geometry option.");
        int[] wh = new int[2];
        for (int i = 0; i < 2; i++) {
            int n = i + 1;
            if (m.group(n) != null)
                wh[i] = Integer.valueOf(m.group(n));
            else
                wh[i] = Geometry.NO_VALUE;
        }
        Geometry.ScalingStrategy scaling = ScalingOption.fromArg(m.group(3));

        return new Geometry(wh[0], wh[1], scaling);
    }

    protected enum ScalingOption {
        OPT1(Geometry.ScalingStrategy.EMPHATIC, "!"),
        OPT2(Geometry.ScalingStrategy.MAXIMUM, ">"),
        OPT3(Geometry.ScalingStrategy.MINIMUM, "<");

        private Geometry.ScalingStrategy strategy;
        private String[] opts;

        private ScalingOption(Geometry.ScalingStrategy strategy, String... opts) {
            this.strategy = strategy;
            this.opts = opts;
        }

        protected static Geometry.ScalingStrategy fromArg(String opt) {
            if (opt == null || opt.isEmpty())
                return Geometry.ScalingStrategy.MAXIMUM;
            for (ScalingOption v : values()) {
                for (int i = 0; i < v.opts.length; i++) {
                    if (v.opts[i].equals(opt))
                        return v.strategy;
                }
            }
            throw new IllegalArgumentException("unknown option: " + opt);
        }
    }

    protected enum FormatOption {
        OPT1(ConvertOption.Format.JPEG, ".jpg", ".jpeg"),
        OPT2(ConvertOption.Format.PNG, ".png"),
        OPT3(ConvertOption.Format.GIF, ".gif");

        private ConvertOption.Format format;
        private String[] suffix;

        private FormatOption(ConvertOption.Format format, String... suffix) {
            this.format = format;
            this.suffix = suffix;
        }

        protected static ConvertOption.Format fromPath(String path) {
            if (path == null)
                throw new IllegalArgumentException("Invalid format");
            for (FormatOption v : values()) {
                for (int i = 0; i < v.suffix.length; i++) {
                    if (path.endsWith(v.suffix[i]))
                        return v.format;
                }
            }
            throw new IllegalArgumentException("Unavailable format: " + path);
        }
    }
}
