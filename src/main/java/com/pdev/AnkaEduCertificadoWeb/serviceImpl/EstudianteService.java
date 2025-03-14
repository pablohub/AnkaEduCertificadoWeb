package com.pdev.AnkaEduCertificadoWeb.serviceImpl;

import com.pdev.AnkaEduCertificadoWeb.model.Estudiante;
import com.pdev.AnkaEduCertificadoWeb.model.response.ResponseGenerico;
import com.pdev.AnkaEduCertificadoWeb.repository.EstudianteRepository;
import com.pdev.AnkaEduCertificadoWeb.service.IEstudianteService;
import com.pdev.AnkaEduCertificadoWeb.util.AESEncryption;
import com.pdev.AnkaEduCertificadoWeb.util.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.pdev.AnkaEduCertificadoWeb.util.Constantes.*;
@Service
public class EstudianteService implements IEstudianteService {

    private static final Logger logger = LoggerFactory.getLogger(EstudianteService.class);
    @Autowired
    private EstudianteRepository estudianteRepository;

    @Override
    public ResponseGenerico guardarEstudiante(Estudiante estudiante) {
        logger.info("Guardar estudiante");
        ResponseGenerico responseGenerico = new ResponseGenerico();
        try{
            estudianteRepository.save(estudiante);
            responseGenerico.setCodError(COD_OK);
            responseGenerico.setMensaje("Estudiante Registrado correctamente!");
            responseGenerico.setData(estudiante);
        }catch (Exception ex){
            responseGenerico.setCodError(COD_ERRO);
            responseGenerico.setMensaje("Error al Registrar estudiante");
            logger.error("Error registrar estudiante: " + ex.getMessage());
        }
        return responseGenerico;
    }

    @Override
    public void cargarEstudiantesDesdeExcel(MultipartFile file, String codigoCertificado) {
        try{
            List<Estudiante> estudiantes = excelAListaEstudiantes(file.getInputStream(), codigoCertificado);
            logger.info("#Estudiantes size: " + estudiantes.size());
            for(Estudiante estudiante : estudiantes){
                Estudiante estudianteEncontrado = estudianteRepository.findByCodigo(estudiante.getCodigo());
                if(estudianteEncontrado != null) estudiante.setId(estudianteEncontrado.getId());
            }
            estudianteRepository.saveAll(estudiantes);
        }catch (IOException e){
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    @Override
    public List<Estudiante> listarEstudiantes(String codigoCertificado) {
        logger.info("Listar estudiantes");
        /*return estudianteRepository
                .findByCodigoCertificado(codigoCertificado)
                .stream()
                .sorted(Comparator.comparing(Estudiante::getCodigo, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());*/
        return estudianteRepository.findAll();
    }

    @Override
    public Estudiante obtenerEstudiantePorId(long id) {
        return estudianteRepository.getReferenceById(id);
    }

    @Override
    public List<Estudiante> listarEstudiantesPorIds(String strIds) {
        logger.info("Listar estudiantes por ids");
        List<Long> ids = new ArrayList<>();
        String[] idsArray = strIds.split(",");
        for(String id : idsArray){
            ids.add(Long.parseLong(id));
        }
        return estudianteRepository.findAllById(ids);
    }

    @Override
    public Estudiante obtenerEstudiantePorCodigoEncriptado(String codigoEncriptado) {
        return estudianteRepository.findByCodigoEncriptado(codigoEncriptado);
    }

    @Override
    public void eliminarEstudiantesPorIds(String ids) {
        String[] idsArray = ids.split(",");
        for(String id : idsArray){
            estudianteRepository.deleteById(Long.parseLong(id));
        }
    }

    private List<Estudiante> excelAListaEstudiantes(InputStream is, String codigoCertificado){
        try{
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet("Estudiantes");
            Iterator<Row> rows = sheet.iterator();

            List<Estudiante> estudiantes = new ArrayList<>();

            int rowNumber = 0;
            while(rows.hasNext()){
                Row currentRow = rows.next();

                if(rowNumber == 0){
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Estudiante estudiante = new Estudiante();

                int cellIdx = 0;
                while(cellsInRow.hasNext()){

                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx){
                        case 0:
                            estudiante.setCodigo(currentCell.toString());
                            String dataEncrypted = AESEncryption.encrypt(
                                            currentCell.toString())
                                    .replace("/", "e")
                                    .replace("?", "q")
                                    .replace("=", "i");
                            dataEncrypted = Util.recortarYCompletar(dataEncrypted, Util.generateRandomLetter(), 25);
                            estudiante.setCodigoEncriptado(dataEncrypted);
                            break;
                        case 1:
                            estudiante.setNombres(currentCell.toString());
                            break;
                        case 2:
                            estudiante.setCurso(currentCell.toString());
                            break;
                        case 3:
                            estudiante.setFechaInicio(currentCell.toString());
                            break;
                        case 4:
                            estudiante.setFechaFinalizacion(currentCell.toString());
                            break;
                        case 5:
                            estudiante.setCentroCapacitacion(currentCell.toString());
                            break;
                        case 6:
                            estudiante.setNombrePdf(currentCell.toString());
                            break;
                        case 7:
                            estudiante.setEmail(currentCell.toString());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;

                }

                estudiante.setCodigoCertificado(codigoCertificado.toUpperCase());
                estudiantes.add(estudiante);

            }

            workbook.close();
            return estudiantes;

        }catch (IOException e){
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    private void ReadExcel(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet("Estudiantes");
            Iterator<Row> rows = sheet.iterator();

            System.out.println("Apunto de entrar a loops");

            System.out.println("" + sheet.getLastRowNum());
            for (Row row : sheet) {
                for (Cell cell : row) {
                    System.out.println("Valor: " + cell.toString());
                }
            }

            workbook.close();
            System.out.println("Finalizado");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}