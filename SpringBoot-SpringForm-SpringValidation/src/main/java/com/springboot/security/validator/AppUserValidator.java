package com.springboot.security.validator;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.springboot.security.dao.AppUserDAO;
import com.springboot.security.form.AppUserForm;
import com.springboot.security.model.AppUser;

@Component
public class AppUserValidator implements Validator {

	// common-validator library.
	private EmailValidator emailValidator = EmailValidator.getInstance();

	@Autowired
	private AppUserDAO appUserDAO;

	// Các lớp được hỗ trợ bởi Validator này.
	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == AppUserForm.class;
	}

	@Override
	public void validate(Object target, Errors errors) {
		AppUserForm appUserForm = (AppUserForm) target;

		// Kiểm tra các field của AppUserForm.
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.appUserForm.userName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotEmpty.appUserForm.firstName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotEmpty.appUserForm.lastName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.appUserForm.email");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.appUserForm.password");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotEmpty.appUserForm.confirmPassword");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "NotEmpty.appUserForm.gender");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", "NotEmpty.appUserForm.countryCode");

		if (!this.emailValidator.isValid(appUserForm.getEmail())) {
			// Email không hợp lệ.
			errors.rejectValue("email", "Pattern.appUserForm.email");
		} else if (appUserForm.getUserId() == null) {
			AppUser dbUser = appUserDAO.findAppUserByEmail(appUserForm.getEmail());
			if (dbUser != null) {
				// Email đã được sử dụng bởi tài khoản khác.
				errors.rejectValue("email", "Duplicate.appUserForm.email");
			}
		}

		if (!errors.hasFieldErrors("userName")) {
			AppUser dbUser = appUserDAO.findAppUserByUserName(appUserForm.getUserName());
			if (dbUser != null) {
				// Tên tài khoản đã bị sử dụng bởi người khác.
				errors.rejectValue("userName", "Duplicate.appUserForm.userName");
			}
		}

		if (!errors.hasErrors()) {
			if (!appUserForm.getConfirmPassword().equals(appUserForm.getPassword())) {
				errors.rejectValue("confirmPassword", "Match.appUserForm.confirmPassword");
			}
		}
	}

}