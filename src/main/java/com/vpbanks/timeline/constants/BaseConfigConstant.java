package com.vpbanks.timeline.constants;

import java.util.HashMap;
import java.util.Map;

public class BaseConfigConstant {

	public static final int RESPONSE_STATUS_SUCCESS = 1;
    
	public static final int RESPONSE_STATUS_FAIL = 0;

	public static final int ACTIVE = 1;
	public static final int IN_ACTIVE = 0;
	
	public static Boolean isStopJob = false;
	
	public static Map<String, String> jobMapMonitor = new HashMap<>();

	public static final int PAGE_SIZE_EXECUTED = 1000;
	public static final int PAGE_SIZE_VIEW = 10;
    
	public enum StatusEnum {
		WAITING("WAITING"), PROCESS("PROCESS"), SUCCESS("SUCCESS"),
		FAILED("FAILED"), EXPIRED("EXPIRED"), CANCEL("CANCEL"),
		ERROR("ERROR"), FAILED_BY_SENDING("FAILED_BY_SENDING"), FAILED_BY_EXECUTING("FAILED_BY_EXECUTING");

		private String value;

		StatusEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum RedisKeyEnum {
		TIMELINE_ID_JOB_MONITOR("TIMELINE_ID_JOB_MONITOR"),
		TIMELINE_EVENT_TYPE_VALID("TIMELINE_EVENT_TYPE_VALID");

		private String value;

		RedisKeyEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum TypeGroupEnum {
		EVENT_TYPE("EVENT_TYPE"),;

		private String value;

		TypeGroupEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public enum ChannelCodeEnum {
		BOND_SERVICE("BOND_SERVICE"),
		COPYTRADE_SERVICE("COPYTRADE_SERVICE"),
		FUND_SERVICE("FUND_SERVICE"),;

		private String value;

		ChannelCodeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

}
