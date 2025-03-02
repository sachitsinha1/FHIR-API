/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.wipro.fhir.r4.controller.patient_data_handler;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.wipro.fhir.r4.data.patient_data_handler.PatientDemographicModel_NDHM_Patient_Profile;
import com.wipro.fhir.r4.data.request_handler.ResourceRequestHandler;
import com.wipro.fhir.r4.service.patient_data_handler.PatientDataGatewayService;
import com.wipro.fhir.r4.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping(value = "/patient/data", headers = "Authorization")
public class PatientDataGatewayController {

	@Autowired
	private PatientDataGatewayService patientDataGatewayService;

	@Deprecated
	@CrossOrigin
	@ApiOperation(value = "Patient data feeder - takes patient data(demographic)", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/profile/feed/demographic" }, method = { RequestMethod.POST })
	public String feedPatientDemographicData(
			@RequestBody List<PatientDemographicModel_NDHM_Patient_Profile> ndhm_Patient_Profile_List,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		try {
			if (ndhm_Patient_Profile_List != null && ndhm_Patient_Profile_List.size() > 0) {
				List<PatientDemographicModel_NDHM_Patient_Profile> s = patientDataGatewayService
						.feedPatientProfileToMongoDB(ndhm_Patient_Profile_List);
				if (s != null)
					response.setResponse(new Gson().toJson(s));
				else
					response.setError(5000, "Error in saving data.Please connect with administrator");
			} else
				response.setError(5000, "Empty data. Please provide patient profiles to save");
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
		}

		return response.toString();

	}

	@CrossOrigin
	@ApiOperation(value = "Patient profile search from Mongo, search parameter - healthId, healthIdNo, amritId, externalId, phoneNo, state, district, village", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/profile/search/demographic" }, method = { RequestMethod.POST })
	public String patientDataSearchFromMongo(@RequestBody ResourceRequestHandler resourceRequestHandler,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		try {
			String s = patientDataGatewayService.searchPatientProfileMongo(Authorization, resourceRequestHandler);
			if (s != null)
				response.setResponse(s);
			else
				response.setResponse("patient not found");
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
		}

		return response.toString();

	}

	@Deprecated
	@CrossOrigin
	@ApiOperation(value = "Patient data AMRIT to Mongo", consumes = "application/json", produces = "application/json")
	@RequestMapping(value = { "/profile/AMRIT/demographic" }, method = { RequestMethod.POST })
	public String patientDataAMRIT_To_Mongo(@RequestBody ResourceRequestHandler resourceRequestHandler,
			@RequestHeader(value = "Authorization") String Authorization) {

		OutputResponse response = new OutputResponse();
		try {
			// if (ndhm_Patient_Profile_List != null && ndhm_Patient_Profile_List.size() >
			// 0) {
			String s = patientDataGatewayService.generatePatientProfileAMRIT_SaveTo_Mongo(Authorization,
					resourceRequestHandler);
			if (s != null)
				response.setResponse(s);
			else
				response.setError(5000, "Error in saving data.Please connect with administrator");
//			} else
//				response.setError(5000, "Empty data. Please provide patient profiles to save");
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
		}

		return response.toString();

	}

	@CrossOrigin
	@ApiOperation(value = "Patient profile search from Mongo, all data based on page no", produces = "application/json")
	@RequestMapping(value = { "/searchWithPagination/{pageNo}" }, method = {
			RequestMethod.GET }, produces = MediaType.APPLICATION_JSON)
	public String patientDataSearchFromMongoPagination(@PathVariable("pageNo") Integer pageNo) {

		OutputResponse response = new OutputResponse();
		try {
			String s = patientDataGatewayService.searchPatientProfileMongoPagination(pageNo);
			if (s != null)
				response.setResponse(s);
			else
				response.setResponse("No data found");
		} catch (Exception e) {
			response.setError(5000, e.getMessage());
		}

		return response.toString();

	}

}
