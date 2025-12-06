package configs;

import mappers.AddressMapper;
import mappers.BankMapper;
import mappers.BrandMapper;
import mappers.CardMapper;
import mappers.CategoryMapper;
import mappers.CityMapper;
import mappers.ClientBudgetDetailMapper;
import mappers.ClientBudgetMapper;
import mappers.ClientInvoiceDetailMapper;
import mappers.ClientInvoiceMapper;
import mappers.ClientMapper;
import mappers.ClientPaymentMapper;
import mappers.ClientRemitDetailMapper;
import mappers.ClientRemitMapper;
import mappers.InvoiceCategoryMapper;
import mappers.ProductMapper;
import mappers.ProviderExpenseMapper;
import mappers.ProviderInvoiceMapper;
import mappers.ProviderMapper;
import mappers.ProviderPaymentMapper;
import mappers.SubcategoryMapper;
import mappers.TaxConditionMapper;
import mappers.VatBookMapper;
import mappers.receipts.ClientReceiptMapper;
import mappers.receipts.ProviderReceiptMapper;
import mappers.receipts.ReceiptCardMapper;
import mappers.receipts.ReceiptCashMapper;
import mappers.receipts.ReceiptChequeMapper;
import mappers.receipts.ReceiptRetentionMapper;
import mappers.receipts.ReceiptTransferMapper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MyBatisConfig {
    private static final Logger LOGGER = Logger.getLogger(MyBatisConfig.class.getName());
    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.setUrl(DatabaseMigration.ensureAllowPublicKeyRetrieval(AppConfig.get("db.url", "")));
            ds.setUsername(AppConfig.get("db.user", ""));
            ds.setPassword(AppConfig.get("db.pass", ""));
            ds.addConnectionProperty("allowPublicKeyRetrieval", "true");

            Environment env = new Environment("default", new JdbcTransactionFactory(), ds);
            Configuration conf = new Configuration(env);

            registerMappers(conf);
            loadExternalMapperXml(conf);
            int totalMappers = conf.getMapperRegistry().getMappers().size();

            if (totalMappers == 0) {
                throw new IllegalStateException("No se pudieron registrar las interfaces MyBatis requeridas. "
                        + "Reinstalá la aplicación verificando que el instalador incluya app\\gestion.jar y la carpeta app\\lib completa.");
            }

            LOGGER.info(() -> "Mappers MyBatis registrados: " + totalMappers);
            SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(conf);
        } catch (Exception e) {
            throw new RuntimeException("Error creando MyBatis SqlSessionFactory", e);
        }
    }

    private MyBatisConfig() {}

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }

    private static int registerMappers(Configuration conf) {
        Set<Class<?>> knownMappers = Stream.of(
                        AddressMapper.class,
                        BankMapper.class,
                        BrandMapper.class,
                        CardMapper.class,
                        CategoryMapper.class,
                        CityMapper.class,
                        ClientBudgetDetailMapper.class,
                        ClientBudgetMapper.class,
                        ClientInvoiceDetailMapper.class,
                        ClientInvoiceMapper.class,
                        ClientMapper.class,
                        ClientPaymentMapper.class,
                        ClientRemitDetailMapper.class,
                        ClientRemitMapper.class,
                        InvoiceCategoryMapper.class,
                        ProductMapper.class,
                        ProviderExpenseMapper.class,
                        ProviderInvoiceMapper.class,
                        ProviderMapper.class,
                        ProviderPaymentMapper.class,
                        SubcategoryMapper.class,
                        TaxConditionMapper.class,
                        VatBookMapper.class,
                        ClientReceiptMapper.class,
                        ProviderReceiptMapper.class,
                        ReceiptCardMapper.class,
                        ReceiptCashMapper.class,
                        ReceiptChequeMapper.class,
                        ReceiptRetentionMapper.class,
                        ReceiptTransferMapper.class)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Class<?>> failed = new LinkedHashSet<>();

        knownMappers.forEach(mapper -> {
            if (conf.hasMapper(mapper)) {
                return;
            }

            try {
                conf.addMapper(mapper);
            } catch (Exception e) {
                failed.add(mapper);
                LOGGER.log(Level.WARNING, "No se pudo registrar el mapper " + mapper.getName() + ".", e);
            }
        });

        if (!failed.isEmpty()) {
            LOGGER.warning(() -> "Los siguientes mappers MyBatis no pudieron registrarse: " + failed);
        }

        int total = conf.getMapperRegistry().getMappers().size();

        if (total == 0) {
            LOGGER.warning(() -> "No se encontró ningún mapper MyBatis para registrar. Verificá que gestion.jar esté incluido en app/"
                    + " y que el instalador copie la carpeta app/lib completa.");
        }

        return total;
    }

    private static final Pattern MAPPER_NAMESPACE_PATTERN = Pattern.compile("<mapper[^>]*namespace\\s*=\\s*\"([^\"]+)\"");

    private static void loadExternalMapperXml(Configuration conf) {
        Path external = Path.of("config", "mappers");
        if (!Files.isDirectory(external)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(external)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".xml"))
                    .forEach(path -> {
                        String namespace = extractNamespace(path);
                        Class<?> mapperClass = null;
                        if (namespace != null && !namespace.isBlank()) {
                            removeNamespace(conf, namespace);
                            mapperClass = tryResolveMapper(namespace);
                        }

                        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                            LOGGER.info(() -> "Registrando mapper externo: " + external.relativize(path));
                            XMLMapperBuilder builder = new XMLMapperBuilder(reader, conf, path.toString(), conf.getSqlFragments());
                            builder.parse();
                            if (namespace != null && !namespace.isBlank()) {
                                markResourceAsLoaded(conf, external.relativize(path), namespace);
                                registerMapperIfMissing(conf, mapperClass, namespace);
                            }
                        } catch (Exception e) {
                            throw new IllegalStateException("No se pudo cargar el mapper externo " + path + ".", e);
                        }
                    });
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "No se pudo recorrer la carpeta de mappers externos " + external.toAbsolutePath(), e);
        }
    }

    private static Class<?> tryResolveMapper(String namespace) {
        try {
            return Class.forName(namespace, false, MyBatisConfig.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "No se encontró la interfaz del mapper " + namespace + " en el classpath.", e);
            return null;
        }
    }

    private static void registerMapperIfMissing(Configuration conf, Class<?> mapperClass, String namespace) {
        if (mapperClass == null) {
            return;
        }

        if (conf.hasMapper(mapperClass)) {
            return;
        }

        try {
            conf.addMapper(mapperClass);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No se pudo registrar el mapper externo " + namespace + " tras cargar su XML.", e);
        }
    }

    private static String extractNamespace(Path mapperPath) {
        try {
            String content = Files.readString(mapperPath, StandardCharsets.UTF_8);
            Matcher matcher = MAPPER_NAMESPACE_PATTERN.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "No se pudo leer el namespace del mapper externo " + mapperPath + ".", e);
        }
        LOGGER.warning(() -> "No se encontró el atributo namespace en el mapper externo " + mapperPath + ". Se omitirá la limpieza previa.");
        return null;
    }

    private static void removeNamespace(Configuration conf, String namespace) {
        removeNamespaceEntries(conf, "mappedStatements", namespace, true);
        removeNamespaceEntries(conf, "resultMaps", namespace, true);
        removeNamespaceEntries(conf, "parameterMaps", namespace, true);
        removeNamespaceEntries(conf, "keyGenerators", namespace, true);
        removeNamespaceEntries(conf, "sqlFragments", namespace, true);
        removeNamespaceEntries(conf, "caches", namespace, false);
    }

    private static void removeNamespaceEntries(Configuration conf, String fieldName, String namespace, boolean usePrefix) {
        try {
            Field field = Configuration.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(conf);
            if (value instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, ?> typedMap = (Map<String, ?>) map;
                if (usePrefix) {
                    String prefix = namespace + ".";
                    typedMap.keySet().removeIf(key -> key != null && key.startsWith(prefix));
                } else {
                    typedMap.remove(namespace);
                }
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.FINE, "No se pudieron limpiar las entradas existentes del namespace " + namespace + " en " + fieldName, e);
        }
    }

    private static void markResourceAsLoaded(Configuration conf, Path relativePath, String namespace) {
        String classpathResource = ("mappers/" + relativePath.toString()).replace('\\', '/');
        conf.addLoadedResource(classpathResource);
        conf.addLoadedResource(namespace.replace('.', '/') + ".xml");
        conf.addLoadedResource(namespace);
    }

}
