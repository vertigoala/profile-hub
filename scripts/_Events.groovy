def ant = new AntBuilder()

eventCompileEnd = {
    println "\n- Starting JasperReports *.jrxml compilation process..."
    // define the Jasper Reports Compile Task
    ant.taskdef(name:'reportCompile', classname: 'net.sf.jasperreports.ant.JRAntCompileTask', classpath: projectWorkDir)

    // remove existing jasper files
    ant.delete{
        fileset('dir':'grails-app/conf/reports', 'defaultexcludes':'yes'){
            include('name':'**/*.jasper')
        }
    }

    // create a temporary directory for use by the jasper compiler
    ant.mkdir(dir:'target/jasper')

    // compile the reports
    ant.reportCompile(srcdir:'grails-app/conf/reports', destdir:'grails-app/conf/reports', tempdir:'target/jasper', keepJava:true, xmlvalidation:true){
        include(name:'**/*.jrxml')
    }
    println "-------\n- JasperReports compilation process finished"
}
