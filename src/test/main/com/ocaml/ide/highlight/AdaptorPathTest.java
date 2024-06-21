package com.ocaml.ide.highlight;

import com.ocaml.OCamlBaseTest;
import com.ocaml.ide.highlight.intentions.OCamlMessageAdaptor;
import org.junit.Test;

@SuppressWarnings("JUnit4AnnotatedMethodInJUnit3TestCase")
public class AdaptorPathTest extends OCamlBaseTest {

    @Test
    public void testLinuxLikePath() {
        String message = "Error: The implementation /mnt/c/Users/username/IdeaProjects/untitled10/out/production/untitled10/tmp/errors/mismatch/missing_impl/file.ml\n" +
                "       does not match the interface file.cmi:\n" +
                "       The module `Make' is required but not provided\n" +
                "The implementation /mnt/c/Users/username/IdeaProjects/untitled10/out/production/untitled10/tmp/errors/mismatch/missing_impl/file.ml\n" +
                "\n" +
                "       File \"/mnt/c/Users/username/IdeaProjects/untitled10/out/production/untitled10/tmp/errors/mismatch/missing_impl/file.mli\", line 7, characters 0-62:\n" +
                "         Expected declaration\n" +
                "       The module type `Something' is required but not provided\n" +
                "       File \"/mnt/c/Users/username/IdeaProjects/untitled10/out/production/untitled10/tmp/errors/mismatch/missing_impl/file.mli\", lines 1-5, characters 0-3:\n" +
                "         Expected declaration";
        String expected = "Error: The implementation errors/mismatch/missing_impl/file.ml\n" +
                "       does not match the interface file.cmi:\n" +
                "       The module `Make' is required but not provided\n" +
                "The implementation errors/mismatch/missing_impl/file.ml\n" +
                "\n" +
                "       File \"errors/mismatch/missing_impl/file.mli\", line 7, characters 0-62:\n" +
                "         Expected declaration\n" +
                "       The module type `Something' is required but not provided\n" +
                "       File \"errors/mismatch/missing_impl/file.mli\", lines 1-5, characters 0-3:\n" +
                "         Expected declaration";
        // @pre: must ends with a /
        String rootFolderForTempering = "/mnt/c/Users/username/IdeaProjects/untitled10/out/production/untitled10/tmp/";
        message = OCamlMessageAdaptor.temperPaths(message, rootFolderForTempering);
        assertEquals(expected, message);
    }

    @Test
    public void testWindowsPath() {
        String message = "Error: The implementation C:\\Users\\username\\IdeaProjects\\untitled10\\out\\production\\untitled10\\tmp\\errors\\mismatch\\missing_impl\\file.ml\n" +
                "       does not match the interface file.cmi:\n" +
                "The implementation C:\\Users\\username\\IdeaProjects\\untitled10\\out\\production\\untitled10\\tmp\\errors\\mismatch\\missing_impl\\file.ml\n" +
                "\n" +
                "       The module type `Something' is required but not provided\n" +
                "       File \"C:\\Users\\username\\IdeaProjects\\untitled10\\out\\production\\untitled10\\tmp\\errors\\mismatch\\missing_impl\\file.mli\", lines 1-5, characters 0-3:\n" +
                "         Expected declaration\n" +
                "       The module `Make' is required but not provided\n" +
                "       File \"C:\\Users\\username\\IdeaProjects\\untitled10\\out\\production\\untitled10\\tmp\\errors\\mismatch\\missing_impl\\file.mli\", line 7, characters 0-62:\n" +
                "         Expected declaration";
        String expected = "Error: The implementation errors/mismatch/missing_impl/file.ml\n" +
                "       does not match the interface file.cmi:\n" +
                "The implementation errors/mismatch/missing_impl/file.ml\n" +
                "\n" +
                "       The module type `Something' is required but not provided\n" +
                "       File \"errors/mismatch/missing_impl/file.mli\", lines 1-5, characters 0-3:\n" +
                "         Expected declaration\n" +
                "       The module `Make' is required but not provided\n" +
                "       File \"errors/mismatch/missing_impl/file.mli\", line 7, characters 0-62:\n" +
                "         Expected declaration";
        // @pre must ends with a \\
        String rootFolderForTempering = "C:\\Users\\username\\IdeaProjects\\untitled10\\out\\production\\untitled10\\tmp\\";
        message = OCamlMessageAdaptor.temperPaths(message, rootFolderForTempering);
        assertEquals(expected, message);
    }

}
