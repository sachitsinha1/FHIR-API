package com.wipro.fhir.r4.service.care_context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wipro.fhir.r4.repo.healthID.BenHealthIDMappingRepo;
import com.wipro.fhir.r4.service.common.CommonServiceImpl;
import com.wipro.fhir.r4.service.healthID.HealthIDServiceImpl;
import com.wipro.fhir.r4.service.ndhm.LinkCareContext_NDHMService;
import com.wipro.fhir.r4.utils.exception.FHIRException;
import com.wipro.fhir.r4.utils.http.HttpUtils;

@Service
public class CareContextServiceImpl implements CareContextService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Autowired
	HealthIDServiceImpl healthID;
	@Autowired
	private BenHealthIDMappingRepo benHealthIDMappingRepo;
	@Autowired
	private CommonServiceImpl commonServiceImpl;

	HttpUtils httpUtils = new HttpUtils();
	@Autowired
	private LinkCareContext_NDHMService linkCareContext_NDHMService;

	@Override
	public String generateOTPForCareContext(String request) throws FHIRException {
		String OTPres = null;
		try {
			OTPres = linkCareContext_NDHMService.generateOTPForCareContext(request);
		} catch (Exception e) {
			throw new FHIRException(e.getMessage());
		}
		if (OTPres != null)
			return OTPres;
		else
			throw new FHIRException("NDHM_FHIR Error while generating OTP");
	}

	@Override
	public String validateOTPAndCreateCareContext(String request) throws FHIRException {
		String res = null;
		try {
			String ndhmOTPToken = linkCareContext_NDHMService.validateOTPForCareContext(request);
			if (ndhmOTPToken != null) {
				res = linkCareContext_NDHMService.addCareContext(request, ndhmOTPToken);
				if (res != null) {
					Integer a = updateHealthIDWithVisit(request);
					if (a > 0)
						logger.info("NDHM_FHIR Care context linked and ABHA updated successfully");
					else
						logger.info("NDHM_FHIR Error while updating ABHA");
				}
			} else
				throw new FHIRException("NDHM_FHIR Error while validating OTP");
		} catch (Exception e) {
			throw new FHIRException(e.getMessage());
		}
		// commenting 21-03-2022
//		try {
//			logger.info("saving care-context to mongo. Request data : " + request);
//			int i = commonServiceImpl.addCareContextToMongo(request);
//			if (i == 1)
//				logger.info("care-context data saved to mongo");
//		} catch (Exception e) {
//			logger.error("NDHM_FHIR error in saving care-context to mongo");
//		}
		if (res != null)
			return res;
		else
			throw new FHIRException("NDHM_FHIR Error while adding care context");
	}

	/*
	 * shubham shekhar updating the ABHA for the Visit Code
	 */
	Integer updateHealthIDWithVisit(String request) {
		JsonObject jsnOBJ = new JsonObject();
		JsonParser jsnParser = new JsonParser();
		JsonElement jsnElmnt = jsnParser.parse(request);
		jsnOBJ = jsnElmnt.getAsJsonObject();
		Integer result = 0;

		if ((jsnOBJ.has("healthID") && jsnOBJ.get("healthID") != null && !jsnOBJ.get("healthID").isJsonNull())
				&& (jsnOBJ.has("healthIdNumber") && jsnOBJ.get("healthIdNumber") != null
						&& !jsnOBJ.get("healthIdNumber").isJsonNull())) {
//			System.out.println("Passing ABHA and ABHA Number");
			result = benHealthIDMappingRepo.updateHealthIDAndHealthIDNumberForCareContext(
					jsnOBJ.get("healthID").getAsString(), jsnOBJ.get("healthIdNumber").getAsString(),
					jsnOBJ.get("visitCode").getAsString());
		} else if (jsnOBJ.has("healthID") && jsnOBJ.get("healthID") != null && !jsnOBJ.get("healthID").isJsonNull()) {

			System.out.println("Passing ABHA" + jsnOBJ.get("healthID"));
			result = benHealthIDMappingRepo.updateHealthIDForCareContext(jsnOBJ.get("healthID").getAsString(),
					jsnOBJ.get("visitCode").getAsString());
		} else if (jsnOBJ.has("healthIdNumber") && jsnOBJ.get("healthIdNumber") != null
				&& !jsnOBJ.get("healthIdNumber").isJsonNull()) {
//						System.out.println("Passing ABHA Number" + jsnOBJ.get("healthIdNumber"));
			result = benHealthIDMappingRepo.updateHealthIDNumberForCareContext(
					jsnOBJ.get("healthIdNumber").getAsString(), jsnOBJ.get("visitCode").getAsString());
		} else {
			logger.info("ABHA/ABHA Number is null or invalid");
		}

		return result;
	}

}