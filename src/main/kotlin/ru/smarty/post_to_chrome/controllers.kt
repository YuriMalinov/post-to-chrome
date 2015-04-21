package ru.smarty.post_to_chrome

import jet.runtime.typeinfo.JetValueParameter
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.ResultSetMapperFactory
import org.skife.jdbi.v2.StatementContext
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.tweak.ResultSetMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.sql.ResultSet
import java.util.concurrent.ConcurrentHashMap

RequestMapping(value = array("/"))
Controller open class MainController [Autowired](val dbi: DBI) {
    RequestMapping(value = array("/projects"), method = array(RequestMethod.GET))
    ResponseBody fun list(): List<Project> {
        return dbi.withHandle { h ->
            val projectDao = h.attach(javaClass<ProjectDao>())
            projectDao.projects()
        }
    }
    /*
        RequestMapping(value = array("/projects"), method = array(RequestMethod.POST))
        ResponseBody fun create(
                RequestParam(value = "id", defaultValue = "-1") id: Int,
                RequestBody Valid project: Project, errors: BindingResult): Any {

            if (errors.hasErrors()) {
                return errors.toString();
            }

            if (id == -1) {
                jdbc.query("insert into ptc.project (name) values (?, ?) returning id",
                        { ps -> ps.setInt("name", ) }, { ps, rn -> ps.getInt(1) })
            }
        }*/
}

trait ProjectDao {
    SqlQuery("select id, name from ptc.project")
    fun projects(): List<Project>
}

jdbi data class Project(var id: Int, val name: String)


class KotlinDataMapperFactory : ResultSetMapperFactory {
    private val cache = ConcurrentHashMap<Class<*>, KotlinDataMapper>()

    synchronized override fun mapperFor(type: Class<*>, ctx: StatementContext): ResultSetMapper<*> {
        val result = cache.get(type)
        return if (result == null) {
            val n = KotlinDataMapper(type)
            cache.put(type, n)
            n
        } else {
            result
            // TODO: computeIfAbsent, but some problems with Kotlin & java 8.
        }
    }

    override fun accepts(type: Class<*>, ctx: StatementContext) =
            type.getAnnotation(javaClass<jdbi>()) != null
                    && type.getConstructors().size() == 1
}

Retention(RetentionPolicy.RUNTIME)
Target(ElementType.TYPE)
annotation class jdbi

class KotlinDataMapper(type: Class<*>) : ResultSetMapper<Any> {
    private val ctor = type.getConstructors()[0]
    private val x = arrayOfNulls(1, 2, 3)
    private val params = ctor.getParameters().filter{ param ->
        val ann = param.getAnnotation(javaClass<JetValueParameter>())
        if (ann == null) {
            throw RuntimeException("Can't find @JetValueParameter annotation for param #${param.getName()} in ${type.getName()}.")

        }
        ann.name()
    }

    override fun map(index: Int, r: ResultSet, ctx: StatementContext): Any? {
        return ctor.newInstance(*(params.map { r.getObject(it) }).copyToArray())
    }
}