package com.github.tachesimazzoca.imgconv.servlet.request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;

import javax.servlet.http.HttpServletRequest;

import com.github.tachesimazzoca.imgconv.ConvertOption.Format;
import com.github.tachesimazzoca.imgconv.Geometry;

/**
 * A client uses the {@code DeviceFactory} to create a {@link Device} object.
 */
public class DeviceFactory {
    private static final Detector[] detectors = new Detector[] {
            new DocomoDetector(),
            new AuDetector(),
            new SoftbankDetector(),
            new WillcomDetector()
    };

    /**
     * Create a new {@link Device} object from the {@code HttpServletRequest}
     * object.
     * 
     * @param request The {@code HttpServletRequest} object
     * @return A {@link Device} object created from the
     *         {@code HttpServletRequest} object
     */
    public static Device create(HttpServletRequest request) {
        for (int i = 0; i < detectors.length; i++) {
            Optional<Device> deviceOpt = detectors[i].detect(request);
            if (deviceOpt.isPresent())
                return deviceOpt.get();
        }
        return new Device(Device.Group.OTHER,
                Geometry.NO_VALUE, Geometry.NO_VALUE, new Format[0]);
    }

    private interface Detector {
        public Optional<Device> detect(HttpServletRequest request);
    }

    private static abstract class AbstractDetector implements Detector {
        private final Device.Group group;
        private final String userAgentPattern;
        private final Format[] formats;

        private AbstractDetector(Device.Group group,
                String userAgentPattern, Format... formats) {
            this.group = group;
            this.userAgentPattern = userAgentPattern;
            this.formats = formats;
        }

        @Override
        public Optional<Device> detect(HttpServletRequest request) {
            String ua = request.getHeader("user-agent");
            if (ua == null)
                return Optional.absent();
            Pattern ptn = Pattern.compile(userAgentPattern);
            Matcher m = ptn.matcher(ua);
            if (!m.matches())
                return Optional.absent();
            // Use the QVGA display as default.
            return Optional.of(new Device(group, 240, 320, formats));
        }
    }

    private static class DocomoDetector extends AbstractDetector {
        public DocomoDetector() {
            super(Device.Group.DOCOMO, "^DoCoMo/\\d\\.\\d[ /].+$",
                    Format.JPEG, Format.GIF);
        }
    }

    private static class AuDetector extends AbstractDetector {
        public AuDetector() {
            super(Device.Group.AU, "^(?:KDDI-[A-Z]+\\d+[A-Z]? )?UP\\.Browser/.+$",
                    Format.JPEG, Format.GIF, Format.PNG);
        }
    }

    private static class SoftbankDetector implements Detector {
        public Optional<Device> detect(HttpServletRequest request) {
            String ua = request.getHeader("user-agent");
            if (ua == null)
                return Optional.absent();
            Pattern ptn = Pattern.compile("^(?:(?:SoftBank|Vodafone|J-PHONE)/\\d\\.\\d|MOT-).+$");
            Matcher m = ptn.matcher(ua);
            if (!m.matches())
                return Optional.absent();
            // Use QVGA as default
            int w = 240;
            int h = 320;
            String disp = request.getHeader("x-jphone-display");
            if (disp != null) {
                Pattern displayPattern = Pattern.compile("^([0-9]{1,4})\\*([0-9]{1,4})$");
                Matcher displayMatcher = displayPattern.matcher(disp);
                if (displayMatcher.matches()) {
                    w = Integer.valueOf(displayMatcher.group(1));
                    h = Integer.valueOf(displayMatcher.group(2));
                }
            }
            return Optional.of(new Device(Device.Group.SOFTBANK, w, h,
                    new Format[] { Format.JPEG, Format.PNG }));
        }
    }

    private static class WillcomDetector extends AbstractDetector {
        public WillcomDetector() {
            super(Device.Group.WILLCOM, "^Mozilla/3\\.0\\((?:DDIPOCKET|WILLCOM);.+$",
                    Format.JPEG, Format.GIF, Format.PNG);
        }
    }
}
