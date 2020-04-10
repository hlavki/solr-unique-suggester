# Unique Analyzing Infix Suggester

Suggester based on AnalyzingInfixSuggester deduplicates data on index time.
To handle uniqueness of value it uses key-value map, where key is defined by `idValuePattern` configuration attribute.
Map is stored to index in the similar way as values in AnalyzingInfixSuggester.

Configuration attribute `idValuePattern` contains pattern which defines unique key of suggested value. It consists of 4 letters
where every letter defines one of value files:
* **T** - term field value
* **P** - payload field value
* **C** - context field value (valid only if contexts field contains single value)
* **W** - weight field value

For example if `idValuePattern` value is defined as `TC` then key consists from values of term field and context field.
Default vaule is `T`.

Value of key is concatenated, hashed by murmur3 algorithm and then stored to index.

### Note!

I didn't test it on SolrCloud with multiple shards, but I think it won't work!

**Example of usage:**

For example you have phone calls list of employees from multiple organizations and you want to suggest employee name per organization.
Suppose that there are many call records for every employee:

```xml
<searchComponent name="suggest-employee-name" class="solr.SuggestComponent">
        <lst name="suggester">
            <str name="name">employeeNameSuggester</str>
            <str name="lookupImpl">eu.hlavki.solr.suggester.UniqueAnalyzingInfixLookupFactory</str>
            <str name="dictionaryImpl">DocumentDictionaryFactory</str>
            <str name="field">employeeName</str>
            <str name="contextField">organizationId</str>
            <str name="idValuePattern">TC</str>
            <str name="suggestAnalyzerFieldType">suggest_text</str> <!-- choose own field type -->
            <str name="allTermsRequired">false</str>
            <str name="exactMatchFirst">true</str>
            <int name="minPrefixChars">3</int>
            <str name="highlight">false</str>
        </lst>
    </searchComponent>
    <requestHandler name="/suggest/employee" class="solr.SearchHandler">
        <lst name="defaults">
            <str name="suggest">true</str>
            <str name="suggest.dictionary">employeeNameSuggester</str>
            <bool name="suggest.highlight">true</bool>
            <int name="suggest.count">5</int>
            <str name="wt">json</str>
        </lst>
        <arr name="components">
            <str>suggest-employee-name</str>
        </arr>
    </requestHandler>
```

Another example is junit test [UniqueSuggesterSearchTest](src/test/java/eu/hlavki/solr/suggester/UniqueSuggesterSearchTest.java)
