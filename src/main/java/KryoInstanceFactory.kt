import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.koin.core.context.GlobalContext
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.Definition
import org.koin.core.definition.Kind
import org.koin.core.instance.InstanceContext
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.StringQualifier
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID
import java.awt.Dimension
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun main() {
    val saveFile = File("file.bin")
    val kryo = buildKryo()

    val smthg: Definition<Dimension> = { it -> Dimension() }

    val instFac = SingleInstanceFactory(
        BeanDefinition(
            StringQualifier("foo"),
            Dimension::class,
            definition = smthg,
            kind = Kind.Factory
        )
    )

    val output: Output = Output(FileOutputStream(saveFile))
    kryo.writeClassAndObject(output, instFac)
    output.close()

    println("restoring...")
    val input = Input(FileInputStream(saveFile));
    val restored: SingleInstanceFactory<Dimension> = kryo.readClassAndObject(input) as SingleInstanceFactory<Dimension>

    // invoke the lambda in the factory to build the objet
    val koin = GlobalContext.startKoin {}.koin
    val result =
        restored.get(InstanceContext(koin, Scope(StringQualifier("foo"), ScopeID(), false, koin), ParametersHolder()))
    println("result is ${result}")
}


private fun buildKryo(): Kryo {

    val kryo = Kryo()

    kryo.setReferences(true)

    // https://github.com/EsotericSoftware/kryo/issues/196
    kryo.isRegistrationRequired = false

//    kryo.register(Dimension::class.java)
//
//    kryo.register(ArrayList::class.java)
//    kryo.register(ArrayDeque::class.java)
//
//    kryo.register(Koin::class.java)
//    kryo.register(EmptyLogger::class.java)
//    kryo.register(Level::class.java)
//    kryo.register(HashSet::class.java)
//    kryo.register(Module::class.java)
//    kryo.register(PropertyRegistry::class.java)
//    kryo.register(StringQualifier::class.java)
//    kryo.register(Scope::class.java)
//    kryo.register(ScopeRegistry::class.java)
//
//    kryo.register(BeanDefinition::class.java)
//    kryo.register(SingleInstanceFactory::class.java)
//    kryo.register(Callbacks::class.java)
//    kryo.register(ConcurrentHashMap::class.java)

    //https://github.com/EsotericSoftware/kryo/issues/320
//    kryo.register(load("org.kalasim.Environment\$2\$1"))

    return kryo
}