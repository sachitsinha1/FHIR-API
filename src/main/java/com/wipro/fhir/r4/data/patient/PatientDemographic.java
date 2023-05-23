package com.wipro.fhir.r4.data.patient;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class PatientDemographic {
	private String healthID;
	private String healthIdNo;
	private Long beneficiaryRegID;
	private Long beneficiaryID;

	private String firstName;
	private String middleName;
	private String lastName;
	private String name;

	private Integer genderID;
	private String gender;

	private Integer maritalStatusID;
	private String maritalStatus;

	private String preferredPhoneNo;

	private String country;
	private String state;
	private String district;
	private String village;

	private Timestamp dOB;

	private String email;
	private String fatherName;
	private Integer actualAge;
	private String ageUnits;

	public PatientDemographic(String healthID, String healthIdNo, Long beneficiaryRegID, Long beneficiaryID,
			String name, Integer genderID, String gender, Integer maritalStatusID, String maritalStatus, Timestamp dOB,
			String preferredPhoneNo, String country, String state, String district) {
		super();
		this.healthID = healthID;
		this.healthIdNo = healthIdNo;
		this.beneficiaryRegID = beneficiaryRegID;
		this.beneficiaryID = beneficiaryID;

		this.name = name;
		this.genderID = genderID;
		this.gender = gender;
		this.maritalStatusID = maritalStatusID;
		this.maritalStatus = maritalStatus;

		this.dOB = dOB;
		this.preferredPhoneNo = preferredPhoneNo;

		this.country = country;
		this.state = state;
		this.district = district;

	}

	public PatientDemographic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PatientDemographic getPatientDemographic(List<Object[]> objList) {
		if (objList != null && objList.size() > 0) {
			Object[] objArr = objList.get(objList.size() - 1);
			PatientDemographic obj = new PatientDemographic((String) objArr[0], (String) objArr[1],
					((BigInteger) objArr[2]).longValue(), ((BigInteger) objArr[3]).longValue(), (String) objArr[4],
					(Integer) objArr[5], (String) objArr[6], (Integer) objArr[7], (String) objArr[8],
					(Timestamp) objArr[9], (String) objArr[10], (String) objArr[11], (String) objArr[12],
					(String) objArr[13]);
			return obj;
		} else
			return null;

	}

}