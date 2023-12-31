package dev.ngdangkiet.mapper;

import dev.ngdangkiet.dkmicroservices.department.protobuf.PDepartment;
import dev.ngdangkiet.dkmicroservices.employee.protobuf.PEmployee;
import dev.ngdangkiet.domain.EmployeeEntity;
import dev.ngdangkiet.enums.department.Department;
import dev.ngdangkiet.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * @author ngdangkiet
 * @since 10/31/2023
 */

@Mapper(config = ProtobufMapperConfig.class)
public interface EmployeeMapper extends ProtobufMapper<EmployeeEntity, PEmployee> {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Named("mapString2LocalDate")
    static LocalDate mapString2LocalDate(String birthDay) {
        return DateTimeUtil.convert2Localdate(birthDay);
    }

    @Named("mapLocalDate2String")
    static String mapLocalDate2String(LocalDate birthDay) {
        return Objects.nonNull(birthDay) ? birthDay.toString() : EMPTY;
    }

    @Named("mapDepartmentId2PDepartment")
    static PDepartment mapDepartmentId2PDepartment(Long departmentId) {
        return PDepartment.newBuilder()
                .setId(departmentId)
                .setName(Department.valueOf(departmentId).getName())
                .build();
    }

    @Override
    @Mapping(source = "departmentId", target = "department", qualifiedByName = "mapDepartmentId2PDepartment")
    @Mapping(target = "birthDay", qualifiedByName = "mapLocalDate2String")
    PEmployee toProtobuf(EmployeeEntity domain);

    @Override
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(target = "birthDay", qualifiedByName = "mapString2LocalDate")
    EmployeeEntity toDomain(PEmployee protobuf);
}
