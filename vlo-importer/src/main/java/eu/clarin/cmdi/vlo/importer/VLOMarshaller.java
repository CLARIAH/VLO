package eu.clarin.cmdi.vlo.importer;

import eu.clarin.cmdi.vlo.MappingDefinitionResolver;
import eu.clarin.cmdi.vlo.config.VloConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VLOMarshaller {

    private final static Logger logger = LoggerFactory.getLogger(VLOMarshaller.class);

    private final static Map<String, FacetConceptMapping> MAPPING_CACHE = new ConcurrentHashMap<>();

    /**
     * Get facet concepts mapping from a facet concept mapping file. Unmarshalled
     * mappings are cached statically for this class.
     *
     * @param facetConcepts name of the facet concepts file
     * @return the facet concept mapping
     */
    public static FacetConceptMapping getFacetConceptMapping(String facetConcepts) {
        if (!MAPPING_CACHE.containsKey(facetConcepts)) {
            // unmarshall map for file
            final MappingDefinitionResolver mappingDefinitionResolver
                    = new MappingDefinitionResolver(VLOMarshaller.class);

            FacetConceptMapping result;
            InputStream is = null;

            try {
                is = (facetConcepts == null || "".equals(facetConcepts))
                        ? VLOMarshaller.class.getResourceAsStream(VloConfig.DEFAULT_FACET_CONCEPTS_RESOURCE_FILE)
                        : mappingDefinitionResolver.tryResolveUrlFileOrResourceStream(facetConcepts);
            } catch (FileNotFoundException e) {
                logger.error("Could not find facets file: {}", facetConcepts);
                return null;
            } catch (IOException e) {
                logger.error("Could not process facets file: {}", facetConcepts);
                return null;
            }
            MAPPING_CACHE.put(facetConcepts, unmarshal(is));
        }
        return MAPPING_CACHE.get(facetConcepts);
    }

    /**
     * Get object from input stream
     *
     * @param inputStream
     * @return
     */
    static FacetConceptMapping unmarshal(InputStream inputStream) {
        FacetConceptMapping result;

        try {
            JAXBContext jc = JAXBContext.newInstance(FacetConceptMapping.class);
            Unmarshaller u = jc.createUnmarshaller();
            result = (FacetConceptMapping) u.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException();
        }

        result.check();
        return result;
    }

    /**
     * Put facet mapping object in output file
     *
     * @param outputFile
     * @return
     */
    static String marshal(FacetConceptMapping outputFile) {
        try {
            JAXBContext jc = JAXBContext.newInstance(FacetConceptMapping.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(outputFile, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
