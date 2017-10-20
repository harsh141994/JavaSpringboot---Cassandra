package com.worksap.stm.lib;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ScheduleResponseEntity {
	private String scheduleId;
	private long startTime;
	private long endTime;
	private String title;
	private List<String> users;
	private String description;
}
