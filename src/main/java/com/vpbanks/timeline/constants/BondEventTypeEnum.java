package com.vpbanks.timeline.constants;

import org.springframework.util.StringUtils;

public enum BondEventTypeEnum {
	BOND_PAY_EXPECT_INTEREST("Trả lãi trái phiếu dự kiến"), 
	BOND_PAY_EXPECT_ORIGIN("Trả gốc trái phiếu dự kiến"),
	BOND_PAY_FACT_INTEREST("Trả lãi trái phiếu thực tế"),
	BOND_PAY_FACT_ORIGIN("Trả gốc trái phiếu thực tế"),
	BOND_EXPECTED_INTEREST_RATE_DETERMINATION_DATE("Ngày xác định lãi suất dự kiến"),
	BOND_FACT_INTEREST_RATE_DETERMINATION_DATE("Ngày xác định lãi suất thực tế"),
	BOND_DATE_START_INTEREST("Ngày bắt đầu tính lãi"),
	BOND_DATE_END_INTEREST("Ngày kết thúc tính lãi"),
	BOND_ISSUE_DATE("Ngày công bố thông tin trái phiếu"),
	BOND_CLOSE_DATE_OWNER_EXPECT("Ngày chốt danh sách trái chủ dự kiến"),
	BOND_CLOSE_DATE_OWNER_FACT("Ngày chốt danh sách trái chủ thực tế"),
	BOND_DATE_DISBURSEMENT("Ngày giải ngân trái phiếu"),
	BOND_DATE_EXPIRE("Ngày đáo hạn trái phiếu"),
	BOND_DEPOSITORY("Lưu ký"),
	BOND_BUY("Mua trái phiếu"),
	BOND_MATCHED("Trái phiếu được khớp lệnh"),
	BOND_SALE("Bán trái phiếu"),
	BOND_BLOCK_TRADING("Phong tỏa giao dịch trái phiếu"),
	BOND_RELEASE_TRADING("Giải tỏa giao dịch trái phiếu"),
	BOND_BANKRUPT("Trái phiếu vỡ nợ"),
	BOND_ACCOUNT_BLOCK("Tài khoản khách hàng bị phong tỏa"),
	;

	private String value;

	BondEventTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static BondEventTypeEnum lockUpByValue(String value) {
		if (!StringUtils.hasLength(value)) return null;
		for (BondEventTypeEnum e : BondEventTypeEnum.values()) {
			if (e.getValue().equals(value)) {
				return e;
			}
		}
		return null;
	}

	public static BondEventTypeEnum lockUpByName(String name) {
		if (!StringUtils.hasLength(name)) return null;
		for (BondEventTypeEnum e : BondEventTypeEnum.values()) {
			if (e.name().equals(name)) {
				return e;
			}
		}
		return null;
	}
}
