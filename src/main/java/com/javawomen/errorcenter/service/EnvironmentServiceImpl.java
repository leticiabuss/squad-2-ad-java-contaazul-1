package com.javawomen.errorcenter.service;
 
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javawomen.errorcenter.controller.dto.EnvironmentDto;
import com.javawomen.errorcenter.controller.form.EnvironmentForm;
import com.javawomen.errorcenter.model.Environment;
import com.javawomen.errorcenter.repository.EnvironmentRepository;

@Service
public class EnvironmentServiceImpl implements EnvironmentService{

	@Autowired
	private EnvironmentRepository environmentRepository;

	public List<Environment> findAll() {
		return environmentRepository.findAll();
	}

	public Optional<Environment> findById(Long id) {
		return environmentRepository.findById(id);
	}

	public Environment save(Environment object) {
		return environmentRepository.save(object);
	}

	public void deleteById(Long id) {
		environmentRepository.deleteById(id);		
	}

	public Optional<Environment> findByName(String nameEnvironment) {
		return environmentRepository.findByName(nameEnvironment);
	}
	
	//--------------- métodos que devolvem um dto ------------
	
	public EnvironmentDto converterToEnvironment(Environment environment) {			
		return new EnvironmentDto(environment);
	}
	
	public List<EnvironmentDto> converter(List<Environment> environments) {
		return environments.stream().map(EnvironmentDto::new).collect(Collectors.toList());
	}

	public EnvironmentDto converterToEnvironment(Optional<Environment> environmentOptional) {
		return converterToEnvironment(environmentOptional.get());
	}

	//------------------- métodos que devolvem um FORM --------------------
	
	public Environment converter(EnvironmentForm form) {
			return new Environment(form.getName());
	}
}
