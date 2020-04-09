package eu.hlavki.solr.suggester;

import org.apache.solr.SolrTestCaseJ4;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Michal Hlavac
 */
public class UniqueSuggesterSearchTest extends SolrTestCaseJ4 {

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCore("solrconfig.xml", "managed_schema", "src/test/configsets", "unique_suggester_configs");
        index();
    }

    public static void index() throws Exception {
        assertU(adoc("id", "1", "name", "Peter Lipan", "identifier", "111", "type", "A"));
        assertU(adoc("id", "2", "name", "Peter Lipan", "identifier", "112", "type", "B"));
        assertU(adoc("id", "3", "name", "Anton Lipan", "identifier", "115", "type", "A"));
        assertU(adoc("id", "4", "name", "Anton Lipan", "identifier", "115", "type", "B"));
        assertU(adoc("id", "5", "name", "Jeremiáš Otruba", "identifier", "111", "type", "C"));
        assertU(adoc("id", "6", "name", "Veronika Krátka", "identifier", "114", "type", "A"));

        assertU(commit());
    }

    @Test
    public void suggest() throws Exception {
        assertJQ(req("suggest.q", "pet", "qt", "/suggest/name-id"), "/suggest/UniqueAnalyzingInfixSuggester/pet/numFound==2");
        assertJQ(req("suggest.q", "anto", "qt", "/suggest/name-id"), "/suggest/UniqueAnalyzingInfixSuggester/anto/numFound==1");
        assertJQ(req("suggest.q", "lip", "qt", "/suggest/name-id"), "/suggest/UniqueAnalyzingInfixSuggester/lip/numFound==3");
    }

    @Test
    public void suggestContext() throws Exception {
        assertJQ(req("suggest.q", "pete", "suggest.cfq", "B", "qt", "/suggest/name-id"), "/suggest/UniqueAnalyzingInfixSuggester/pete/numFound==1");
    }
}
