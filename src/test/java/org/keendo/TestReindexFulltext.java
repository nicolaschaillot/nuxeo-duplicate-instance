/*
 * Contributors:
 *     Nicolas Chaillot
 */
package org.keendo;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class TestDuplicateInstance {

    @Inject
    protected CoreSession session;

    @Test
    public void testReindexFulltext() throws Exception {
        // create a live doc
        DocumentModel file = new DocumentModelImpl("/", "file", "File");
        file = session.createDocument(file);

        // create a version
        DocumentRef ver = session.checkIn(file.getRef(), VersioningOption.MINOR, null);

        // create a proxy (not reindexed)
        session.createProxy(ver, session.getRootDocument().getRef());

        // create an unfiled doc
        DocumentModel file2 = new DocumentModelImpl((String) null, "file2", "File");
        session.createDocument(file2);

        session.save();

        ReindexFulltextRoot reindex = new ReindexFulltextRoot();
        reindex.coreSession = session;
        String ok = reindex.reindexFulltext(0, 0);
        assertEquals("done: 3 total: 3 batch_errors: 0", ok);
    }

}
