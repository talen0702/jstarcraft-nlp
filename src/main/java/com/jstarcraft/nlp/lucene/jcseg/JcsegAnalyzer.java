package com.jstarcraft.nlp.lucene.jcseg;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class JcsegAnalyzer extends Analyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JcsegAnalyzer.class);

    private int mode;

    private JcsegTaskConfig config = null;

    private ADictionary dic = null;

    /**
     * initialize the analyzer with the specified mode And Jcseg will look for the default configuration file
     * 
     * @param mode tokenizer mode
     * @see org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig
     */
    public JcsegAnalyzer(int mode) {
        this(mode, new JcsegTaskConfig(true));
    }

    /**
     * initialize the analyzer with the specified mode and the jcseg.properties file
     * 
     * @param mode    tokenizer mode
     * @param proFile path of jcseg.properties file
     */
    public JcsegAnalyzer(int mode, String proFile) {
        this(mode, new JcsegTaskConfig(proFile));
    }

    /**
     * initialize the analyzer with the specified mode and configuration
     * 
     * @param mode   tokenizer mode
     * @param config
     */
    public JcsegAnalyzer(int mode, JcsegTaskConfig config) {
        this(mode, config, DictionaryFactory.createSingletonDictionary(config));
    }

    /**
     * initialize the analyzer with the specifiled mode, configuration, dictionary
     * 
     * @param mode
     * @param config
     * @param dic
     */
    public JcsegAnalyzer(int mode, JcsegTaskConfig config, ADictionary dic) {
        this.mode = mode;
        this.config = config;
        this.dic = dic;
    }

    public void setConfig(JcsegTaskConfig config) {
        this.config = config;
    }

    public void setDict(ADictionary dic) {
        this.dic = dic;
    }

    public JcsegTaskConfig getTaskConfig() {
        return config;
    }

    public ADictionary getDict() {
        return dic;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        try {
            Tokenizer tokenizer = new JcsegTokenizer(mode, config, dic);
            return new TokenStreamComponents(tokenizer);
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
