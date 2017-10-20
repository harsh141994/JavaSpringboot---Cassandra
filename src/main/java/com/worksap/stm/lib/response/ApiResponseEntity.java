package com.worksap.stm.lib.response;

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
public class ApiResponseEntity<T> {
	/**
	 * Use ApiStatus for appropriate status
	 */
	private String status;
	
	private T details;
	
	/**
	 * Use ApiFailureCause for appropriate status
	 */
	private String cause;
}
