package com.vpbanks.timeline.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCustom<T> {

	private Integer totalPage;
	private Long totalItem;
	private List<T> content;
	
	public PageCustom(Page<T> page) {
		this.totalPage = page.getTotalPages();
		this.totalItem = page.getTotalElements();
		this.content = page.getContent();
	}
}
