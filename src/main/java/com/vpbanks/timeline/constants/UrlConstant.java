package com.vpbanks.timeline.constants;

public class UrlConstant {

	private UrlConstant() {
	}

	public static final String V1_BASIC_ADMIN_URL = "/admin/v1";

	public static final String V1_BASIC_URL = "/v1";

	public static final String UPDATE_CACHE_URL = "/update/cache";

	public static final String SCHEDULE_EXECUTED_URL = "/schedule/executed";
	
	/*** EventManagementController ***/
	public static final String EVENT_MANAGEMENT_URL = "/event-management";

	/** api lấy thông tin event **/
	public static final String FIND_EVENT = "/find-events";
	/*** EventManagementController ***/
	
	public static final String GET_EVENTS_BY_USER_ID = "/get-events-by-user-id";

	public static final String UPDATE_EVENTS_BY_EVENT_ID = "/update-events-by-event-id";

	public static final String GET_EVENTS_EVENT_ID = "/get-events-event-id";

	public static final String CANCEL_EVENTS = "/cancel-events";

	public static final String SAVE_EVENTS = "/save-events";

	public static final String SEND_EVENTS = "/send-event";

	public static final String GET_JOB = "/get-job";
	
	public static final String STOP_ALL_JOB = "/stop-all-job";
	
	public static final String STOP_JOB_MONITOR = "/stop-job-monitor";

	public static final String SAVE_EVENTS_BOND = "/save-events-bond";

	public static final String RESTART_JOB = "/schedule/restart";

	public static final String GET_EVENT_TYPE = "/get-event-type";

	public static final String GET_CHANNEL_CODE = "/get-channel-code";
}
