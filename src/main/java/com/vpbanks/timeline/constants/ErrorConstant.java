package com.vpbanks.timeline.constants;

public class ErrorConstant {

    private ErrorConstant() {
	}

	public static final String SUCCESS = "200000";

	public static final String ERR_5001001 = "5001001";

	public static final String BAD_REQUEST = "400000";

	public enum TimeLineErrorCode {
		DATA_ERROR("5001002", "data error"),
		FIELD_REQUIRED("4000000", "field is required"),
		USER_ID_IS_NULL("4000001", "userId is null"),
		EVENT_TYPE_IS_NULL("4000002", "eventType is null"),
		EVENT_TYPE_ID_IS_NULL("4000003", "eventTypeId is null"),
		START_DATE_IS_NULL("4000004", "startDate is null"),
		END_DATE_IS_NULL("4000005", "endDate is null"),
		PRODUCT_CODE_IS_NULL("4000006", "productCode is null"),
		CONTRACT_CODE_IS_NULL("4000007", "contractCode is null"),
		INTEREST_AMOUNT_IS_NULL("4000008", "interestAmount is null"),
		PRINCIPAL_AMOUNT_IS_NULL("4000009", "principalAmount is null"),
		FROM_DATE_REQUIRED("4000010", "fromDate is required"),
		TO_DATE_REQUIRED("4000011", "toDate is required"),
		ACCOUNT_ALREADY_EXIST("4002017", "account_already_exist"),
		CHANNEL_CODE_IS_REQUIRED("4000011", "channel_code is required"),
		PRODUCT_ID_IS_NULL("4000012", "product_id is null"),
		START_DATE_GT_END_DATE("4000013", "startDate greate than endDate"),
		END_DATE_GT_NOW("4000014", "endDate greate than now"),
		START_DATE_INVALID("4000015", "start_date invalid"),
		DATA_NOT_FOUND("4000016", "data not found"),
		EVENT_TYPE_NOT_FOUND("4000017", "Event type not found"),
		EVENT_TRANSFERED_TO_KAFKA("4000018", "Event has been transfered to kafka"),
		REF_ID_REQUIRED("4000019", "reference_id is required"),
		PACKAGE_ID_REQUIRED("4000020", "packageId is required"),
		STATUS_INVALID("4000021", "status invalid"),
		DUPLICATE_EVENT("4000022", "duplicate event"),
		EVENT_TYPE_INVALID("4000023", "Event type invalid"),
		FROM_DATE_LT_TO_DATE("4000024", "From date is less than To date"),
		;

		private String code;
		private String message;

		TimeLineErrorCode(String code, String message) {
			this.code = code;
			this.message = message;
		}

		TimeLineErrorCode() {
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
