package com.vpbanks.timeline.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static java.time.Month.FEBRUARY;

/** Utility library for working with {@link Date}s. */
@Slf4j
public final class DateUtils {

	/** February 29th. */
	static final MonthDay LEAP_DAY = MonthDay.of(FEBRUARY, 29);

	public static final String FORMAT_DDMMYYYY = "dd/MM/yyyy";

	public static final String FORMAT_DDMMYYYYHHMMSS = "yyyy-MM-dd HH:mm:ss";
	
	public static final String FORMAT_DDMMYYYY_T_HHMMSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	public static final String FORMAT_YYYYDDMM = "yyyy-MM-dd";

	private DateUtils() {
	}

	public static Long convertStringDateToLong(String input, String format) {
		try {
			Date date = DateUtils.convertStringToDate(input, format);
			if (Objects.nonNull(date)) {
				LocalDateTime localEndDate = DateUtils.convertDateToLocalDateTime(date);
				return DateUtils.localDateTimeToDate(localEndDate).getTime();
			}
			return null;
		}catch (Exception e){
			log.error("convertStringDateToLong Exception {} - {}", e, e.getMessage());
			return null;
		}
	}
	
	public static Long convertNowToLong() {
		Date date = new Date();
		if (Objects.nonNull(date)) {
			LocalDateTime localEndDate = DateUtils.convertDateToLocalDateTime(date);
			return DateUtils.localDateTimeToDate(localEndDate).getTime();
		}
		return null;
	}
	
	public static Date atStartOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return localDateTimeToDate(startOfDay);
	}

	public static Long getStartOfToday() {
		Date date = new Date();
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
		return localDateTimeToDate(startOfDay).getTime();
	}

	public static Long getStartOfYesterday() {
		Long today = getStartOfToday();
		return today - 24 * 60 * 60 * 1000;
	}

	public static Date atEndOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
		return localDateTimeToDate(endOfDay);
	}

	public static boolean inDay(long timeSeconds) {
		Date date = new Date();
		Date startOfDay = atStartOfDay(date);
		Date endOfDay = atEndOfDay(date);

		return timeSeconds >= startOfDay.getTime() / 1000 && timeSeconds <= endOfDay.getTime() / 1000;
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		return localDateTime != null ? Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()) : null;
	}

	public static Date convertStringToDate(String strDate, String format) {
		if (!StringUtils.hasLength(strDate)) {
			return null;
		}
		SimpleDateFormat SDF_FORMATTED = new SimpleDateFormat(format);
		try {
			Date d = SDF_FORMATTED.parse(strDate);
			return d;
		} catch (Exception e) {
			log.info("[convertStringToDate] exception {} - {}", e, e.getMessage());
		}
		return null;
	}
	
	

	public static String convertDateToString(Date date, String format) {
		if (date == null) {
			return null;
		}

		SimpleDateFormat SDF_FORMATTED = new SimpleDateFormat(format);
		try {
			String str = SDF_FORMATTED.format(date);
			return str;
		} catch (Exception e) {
			log.info("convertDateToString exception {} - {}", e, e.getMessage());
		}
		return null;
	}

	public static LocalDateTime convertStringToLocalDateTime(String dateTime) {
		try {
			return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(FORMAT_YYYYDDMM));
		} catch (Exception e) {
			log.error("Getting error when parse String: {} to LocalDateTime", dateTime);
			return null;
		}
	}

	public static Date addDaysToDate(Date date, long days) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime result = localDateTime.plusDays(days);
		return localDateTimeToDate(result);
	}

	public static Date dateAfterOneHour(Date date, long hours) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime result = localDateTime.plusHours(hours);
		return localDateTimeToDate(result);
	}

	public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
		try {
			return dateTime.format(DateTimeFormatter.ofPattern(FORMAT_DDMMYYYYHHMMSS));
		} catch (Exception e) {
			log.error("Getting error when parse Datetime: {} to String", dateTime);
			return null;
		}
	}

	public static Date atEndOfDayToSecond(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	public static Date addMothsToDate(Date date, Integer months) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime result = localDateTime.plusMonths(months);
		return localDateTimeToDate(result);
	}

	public static String convertDateToIso8601(Date date) {
		TimeZone tz = TimeZone.getTimeZone("GMT+7");
		DateFormat df = new SimpleDateFormat(FORMAT_DDMMYYYY_T_HHMMSS_Z);
		df.setTimeZone(tz);
		String nowAsISO = df.format(date);
		return nowAsISO;
	}

	public static Date convertLocalDateTimeToDate(LocalDateTime datetime) {
		LocalDateTime localDateTime = LocalDateTime.parse(datetime.toString());
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public static LocalDateTime convertDateToLocalDateTime(Date date) {
		try {
			return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (Exception e) {
			log.error("Getting error when parse date: {} to LocalDateTime", date);
			return null;
		}
	}
}
