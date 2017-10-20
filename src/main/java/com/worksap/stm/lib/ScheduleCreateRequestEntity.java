package com.worksap.stm.lib;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequestEntity {
	private long startTime;
	private long endTime;
	private String title;
	private List<String> users;
	private String description;
}
