package com.jstarcraft.nlp.lucene.jieba;

import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

@Deprecated
public class JiebaTokenizerFactory extends TokenizerFactory {

    private JiebaSegmenter.SegMode segMode;

    public JiebaTokenizerFactory(Map<String, String> configuration) {
        super(configuration);
        if (null == configuration.get("segMode")) {
            segMode = SegMode.SEARCH;
        } else {
            segMode = JiebaSegmenter.SegMode.valueOf(configuration.get("segMode"));
        }
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        return new JiebaTokenizer(segMode);
    }

}
