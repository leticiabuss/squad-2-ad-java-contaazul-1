package com.javawomen.errorcenter.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.javawomen.errorcenter.config.validation.ResourceNotFoundException;
import com.javawomen.errorcenter.controller.dto.LogDto;
import com.javawomen.errorcenter.controller.form.LogForm;
import com.javawomen.errorcenter.data.Archive;
import com.javawomen.errorcenter.model.Environment;
import com.javawomen.errorcenter.model.Level;
import com.javawomen.errorcenter.model.Log;
import com.javawomen.errorcenter.repository.LogRepository;

/**
 * @author Letícia
 *
 */

@Service
public class LogService {// implements ServiceInterface<Log> {

	@Autowired
	LogRepository logRepository;

	public Page<Log> findAll(Pageable paginacao) {
		return logRepository.findAll(paginacao);
	}

	public List<Log> findAll() {
		return logRepository.findAll();
	}

	public Page<Log> findByEnvironment(String nameEnvironment, Pageable paginacao) {
		return logRepository.findByEnvironmentName(nameEnvironment, paginacao);
	}

	public Optional<Log> findById(Long id) {
		return logRepository.findById(id);
	}

	public List<Log> findByLevel(String nameLevel) {
		return logRepository.findByLevelName(nameLevel);
	}

	public List<Log> findByEnvironment(String nameEnvironment) {
		return logRepository.findByEnvironmentName(nameEnvironment);
	}

	public List<Log> findByDescription(String description) {
		return logRepository.findByDescription(description);
	}

	public List<Log> findByOrigin(String origin) {
		return logRepository.findByOrigin(origin);
	}

	public Log save(Log object) {
		return logRepository.save(object);
	}

	public void deleteById(Long id) {
		logRepository.deleteById(id);
	}

	// receber uma lista retornar uma lista ordenada com frequencia ASC
	// esse seria o endpoint de eventos, para a última tela, o clietne teria que
	// trabalhar com dois endpoints: buscar o log, e buscar a frequencia
	// public long countByAttribute(String levelName, String environmentName, String
	// originName, String descriptionName){
	// public long countByAttribute(String levelName, String environmentName, String
	// originName, String descriptionName) {

	// --------------------------- FREQUENCY -----------------------------------

	// pegar um log e devolver o numero de vezes que ele aparece no banco
	public Long countByAttribute(Long id) {
		Optional<Log> logOptional = logRepository.findById(id);
		if (!logOptional.isPresent())
			throw new ResourceNotFoundException("ID não encontrado.");
		Log log = logOptional.get();
		return logRepository.countByAllAttribute(log.getLevel().getName(), log.getEnvironment().getName(),
				log.getOrigin(), log.getDescription());
	}

	// pegar todos os logs e devolver ordenado pela frequencia
	public Map<Long, List<LogDto>> countByAttributeList() {

		List<LogDto> frequencyList = new ArrayList<>();
		List<Log> logs = findAll();

		// transforma em DTO
		for (Log log : logs) {
			Long count = countByAttribute(log.getId());
			//LogDto dto = LogDto.converterToLog(log);
			LogDto dto = converterToLog(log);
			dto.setFrequency(count);
			frequencyList.add(dto);
		}
		// compara a frequency ESSA CLASSE INTERNA NAO ESTAH MAIS SENDO USADA, RETIRAR E TESTAR SE ESTÁ TD OK
		class ComparatorDto implements Comparator<LogDto> {
			public int compare(LogDto p1, LogDto p2) {
				return p1.getFrequency() < p2.getFrequency() ? -1 : (p1.getFrequency() > p2.getFrequency() ? +1 : 0);
			}
		}
		// realiza ordenação
		// Comparator<LogDto> crescente = new ComparatorDto();
		// Comparator<LogDto> decrescente = Collections.reverseOrder(crescente);
		// ordena a lista em ordem desc
		// Collections.sort(frequencyList, decrescente);

		// A chave é a frequencia
		Map<Long, List<LogDto>> frequencyMap = frequencyList.stream()
				.collect(Collectors.groupingBy(LogDto::getFrequency));

		// --- inicio ------ ordenação decrescente por chave
		// class MyComparator implements Comparator<Object> { Map map;
		// public MyComparator(Map map) { this.map = map; }
		// public int compare(Object o1, Object o2) {
		// return ((Integer) map.get(o2)).compareTo((Integer) map.get(o1)); } }
		//
		// MyComparator comp = new MyComparator(myMap);
		// Map newMap = new TreeMap(Collections.reverseOrder());
		// newMap.putAll(myMap);
		// --- fim

		return frequencyMap;
	}

	// pegar todos os logs e devolver ordenado pela frequencia
	public Map<Long, List<LogDto>> countByEnvironmentList(String nameEnvironment) {

		List<LogDto> frequencyList = new ArrayList<>();
		List<Log> logs = logRepository.findByEnvironmentName(nameEnvironment);
		
		if(logs.isEmpty()) {
			throw new ResourceNotFoundException("Ambiente não encontrado.");
		}

		// transforma em DTO
		for (Log log : logs) {
			Long count = countByAttribute(log.getId());
			//LogDto dto = LogDto.converterToLog(log);
			LogDto dto = converterToLog(log);
			dto.setFrequency(count);
			frequencyList.add(dto);
		}
		// compara a frequency ESSA CLASSE INTERNA NAO ESTAH MAIS SENDO USADA, RETIRAR E TESTAR SE ESTÁ TD OK
		class ComparatorDto implements Comparator<LogDto> {
			public int compare(LogDto p1, LogDto p2) {
				return p1.getFrequency() < p2.getFrequency() ? -1 : (p1.getFrequency() > p2.getFrequency() ? +1 : 0);
			}
		}

		// A chave é a frequencia
		Map<Long, List<LogDto>> frequencyMap = frequencyList.stream()
				.collect(Collectors.groupingBy(LogDto::getFrequency));

		return frequencyMap;

	}

	
	//------------------- métodos que devolvem um DTO --------------------
	
	//retorna uma lista em paginas de Logs 
	public Page<LogDto> converter(Page<Log> logs) {
		//return logs.stream().map(LogDto::new).collect(Collectors.toList());
		return logs.map(LogDto::new);
	}
	
	//retorna um log (para nao devolver uma entidade)
	public List<LogDto> converterToLog(List<Log> logs) {			
		//return new LogDto(log);
		return logs.stream().map(LogDto::new).collect(Collectors.toList());
	}

	public LogDto converterToLog(Optional<Log> logOptional) {
		//return converterToLog(userOptional.get());
		return new LogDto(logOptional.get());
	}

	public LogDto converterToLog(Log log) {
		return new LogDto(log);
	}
	
	//--------------------------------- métodos que devolvem um FORM -----------------------------------
	public Log converter(LevelService levelService, EnvironmentService environmentService, LogForm form) {
		Optional<Level> levelOptional = levelService.findByName(form.getNameLevel());
		Optional<Environment> environmentOptional = environmentService.findByName(form.getNameEnvironment());

		if(!levelOptional.isPresent())throw new ResourceNotFoundException("Level não encontrado.");
		if(!environmentOptional.isPresent())throw new ResourceNotFoundException("Ambiente não encontrado.");

		return new Log(levelOptional.get(), environmentOptional.get(), form.getOrigin(), form.getDescription());

	}
	
	public void archiveLog(Long id) throws IOException {
		Optional<Log> logOptional = logRepository.findById(id);
		if (!logOptional.isPresent()) throw new ResourceNotFoundException("ID não encontrado.");
		LogDto dto = converterToLog(logOptional);
		Archive archive = new Archive();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmssSSSS");
		String date = dto.getCreatedAt().format(formatter);
		archive.write(dto, date);
	}
	
	//esse método nao é requisito, implementar no controller se der tempo
	public LogDto readArchiveLog(Log log, String archiveName) throws Throwable {
		Archive archive = new Archive();
		LogDto dto = archive.read(archiveName);
		return dto;
	}
	
	
}
