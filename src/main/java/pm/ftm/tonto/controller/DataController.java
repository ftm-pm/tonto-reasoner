package pm.ftm.tonto.controller;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;

@RestController
public class DataController {

    @Nonnull
    private static final String baseOntology = "<?xml version=\"1.0\"?>\n" +
            "<Ontology xmlns=\"http://www.w3.org/2002/07/owl#\"\n" +
            "     xml:base=\"http://www.semanticweb.org/sanny/ontologies/2018/3/untitled-ontology-18\"\n" +
            "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
            "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
            "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
            "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n" +
            "     ontologyIRI=\"http://www.semanticweb.org/sanny/ontologies/2018/3/untitled-ontology-18\">\n" +
            "    <Prefix name=\"owl\" IRI=\"http://www.w3.org/2002/07/owl#\"/>\n" +
            "    <Prefix name=\"rdf\" IRI=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"/>\n" +
            "    <Prefix name=\"xml\" IRI=\"http://www.w3.org/XML/1998/namespace\"/>\n" +
            "    <Prefix name=\"xsd\" IRI=\"http://www.w3.org/2001/XMLSchema#\"/>\n" +
            "    <Prefix name=\"rdfs\" IRI=\"http://www.w3.org/2000/01/rdf-schema#\"/>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Bill\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasUncle\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Max\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "    </Declaration>\n" +
            "    <Declaration>\n" +
            "        <AnnotationProperty IRI=\"http://swrl.stanford.edu/ontologies/3.3/swrla.owl#isRuleEnabled\"/>\n" +
            "    </Declaration>\n" +
            "    <ObjectPropertyAssertion>\n" +
            "        <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "        <NamedIndividual IRI=\"#Max\"/>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "    </ObjectPropertyAssertion>\n" +
            "    <ObjectPropertyAssertion>\n" +
            "        <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "        <NamedIndividual IRI=\"#Tom\"/>\n" +
            "        <NamedIndividual IRI=\"#Bill\"/>\n" +
            "    </ObjectPropertyAssertion>\n" +
            "    <DLSafeRule>\n" +
            "        <Annotation>\n" +
            "            <AnnotationProperty IRI=\"http://swrl.stanford.edu/ontologies/3.3/swrla.owl#isRuleEnabled\"/>\n" +
            "            <Literal datatypeIRI=\"http://www.w3.org/2001/XMLSchema#boolean\">true</Literal>\n" +
            "        </Annotation>\n" +
            "        <Annotation>\n" +
            "            <AnnotationProperty abbreviatedIRI=\"rdfs:comment\"/>\n" +
            "            <Literal datatypeIRI=\"http://www.w3.org/2001/XMLSchema#string\"></Literal>\n" +
            "        </Annotation>\n" +
            "        <Annotation>\n" +
            "            <AnnotationProperty abbreviatedIRI=\"rdfs:label\"/>\n" +
            "            <Literal datatypeIRI=\"http://www.w3.org/2001/XMLSchema#string\">Rule hasUncle</Literal>\n" +
            "        </Annotation>\n" +
            "        <Body>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasParent\"/>\n" +
            "                <Variable IRI=\"x\"/>\n" +
            "                <Variable IRI=\"y\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasBrother\"/>\n" +
            "                <Variable IRI=\"y\"/>\n" +
            "                <Variable IRI=\"z\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "        </Body>\n" +
            "        <Head>\n" +
            "            <ObjectPropertyAtom>\n" +
            "                <ObjectProperty IRI=\"#hasUncle\"/>\n" +
            "                <Variable IRI=\"x\"/>\n" +
            "                <Variable IRI=\"z\"/>\n" +
            "            </ObjectPropertyAtom>\n" +
            "        </Head>\n" +
            "    </DLSafeRule>\n" +
            "</Ontology>\n";


    @RequestMapping(value = "/api/ontology", method = RequestMethod.POST)
    public String greeting(@RequestParam(value="ontology", defaultValue="") String ontology) {
        if (ontology.length() == 0) {
            ontology = baseOntology;
        }
        try {
            shouldUseReasoner(ontology);
        } catch (OWLOntologyCreationException exp) {
            System.out.print(exp.getMessage());
        }

        return "Answer ontology 111";
    }

    /**
     * @param manager
     *        manager
     * @return loaded ontology
     * @throws OWLOntologyCreationException
     *         if a problem pops up
     */
    @Nonnull
    private OWLOntology load(@Nonnull OWLOntologyManager manager, String ontology) throws OWLOntologyCreationException {
        return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(ontology));
    }

    /**
     * @param ontologyString
     * @throws OWLOntologyCreationException
     */
    private void shouldUseReasoner(String ontologyString) throws OWLOntologyCreationException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // Now load the local copy
        OWLOntology ont = load(manager, ontologyString);

        System.out.println("Loaded " + ont.getOntologyID());
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory. Create a reasoner factory. In this case, we will use HermiT,
        // but we could also use FaCT++ (http://code.google.com/p/factplusplus/)
        // or Pellet(http://clarkparsia.com/pellet) Note that (as of 03 Feb
        // 2010) FaCT++ and Pellet OWL API 3.0.0 compatible libraries are
        // expected to be available in the near future). For now, we'll use
        // HermiT HermiT can be downloaded from http://hermit-reasoner.com Make
        // sure you get the HermiT library and add it to your class path. You
        // can then instantiate the HermiT reasoner factory: Comment out the
        // first line below and uncomment the second line below to instantiate
        // the HermiT reasoner factory. You'll also need to import the
        // org.semanticweb.HermiT.Reasoner package.
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        // OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
        // We'll now create an instance of an OWLReasoner (the implementation
        // being provided by HermiT as we're using the HermiT reasoner factory).
        // The are two categories of reasoner, Buffering and NonBuffering. In
        // our case, we'll create the buffering reasoner, which is the default
        // kind of reasoner. We'll also attach a progress monitor to the
        // reasoner. To do this we set up a configuration that knows about a
        // progress monitor. Create a console progress monitor. This will print
        // the reasoner progress out to the console.
        ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
        // Specify the progress monitor via a configuration. We could also
        // specify other setup parameters in the configuration, and different
        // reasoners may accept their own defined parameters this way.
        OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
        // Create a reasoner that will reason over our ontology and its imports
        // closure. Pass in the configuration.
        OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
        // Ask the reasoner to do all the necessary work now
        reasoner.precomputeInferences();
        // We can determine if the ontology is actually consistent (in this
        // case, it should be).
        boolean consistent = reasoner.isConsistent();
        System.out.println("Consistent: " + consistent);
        System.out.println("\n");
        // We can easily get a list of unsatisfiable classes. (A class is
        // unsatisfiable if it can't possibly have any instances). Note that the
        // getUnsatisfiableClasses method is really just a convenience method
        // for obtaining the classes that are equivalent to owl:Nothing. In our
        // case there should be just one unsatisfiable class - "mad_cow" We ask
        // the reasoner for the unsatisfiable classes, which returns the bottom
        // node in the class hierarchy (an unsatisfiable class is a subclass of
        // every class).
//        Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
        // This node contains owl:Nothing and all the classes that are
        // equivalent to owl:Nothing - i.e. the unsatisfiable classes. We just
        // want to print out the unsatisfiable classes excluding owl:Nothing,
        // and we can used a convenience method on the node to get these
//        Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
//        if (!unsatisfiable.isEmpty()) {
//            System.out.println("The following classes are unsatisfiable: ");
//            for (OWLClass cls : unsatisfiable) {
//                System.out.println("    " + cls);
//            }
//        } else {
//            System.out.println("There are no unsatisfiable classes");
//        }
//        System.out.println("\n");
        // Now we want to query the reasoner for all descendants of vegetarian.
        // Vegetarians are defined in the ontology to be animals that don't eat
        // animals or parts of animals.
        OWLDataFactory fac = manager.getOWLDataFactory();
        // Get a reference to the vegetarian class so that we can as the
        // reasoner about it. The full IRI of this class happens to be:
        // <http://owl.man.ac.uk/2005/07/sssw/people#vegetarian>
        // OWLClass vegPizza = fac.getOWLClass(IRI.create("man"));
        // Now use the reasoner to obtain the subclasses of vegetarian. We can
        // ask for the direct subclasses of vegetarian or all of the (proper)
        // subclasses of vegetarian. In this case we just want the direct ones
        // (which we specify by the "true" flag).
        // NodeSet<OWLClass> subClses = reasoner.getSubClasses(vegPizza, true);


        // The reasoner returns a NodeSet, which represents a set of Nodes. Each
        // node in the set represents a subclass of vegetarian pizza. A node of
        // classes contains classes, where each class in the node is equivalent.
        // For example, if we asked for the subclasses of some class A and got
        // back a NodeSet containing two nodes {B, C} and {D}, then A would have
        // two proper subclasses. One of these subclasses would be equivalent to
        // the class D, and the other would be the class that is equivalent to
        // class B and class C. In this case, we don't particularly care about
        // the equivalences, so we will flatten this set of sets and print the
        // result
        //Set<OWLClass> clses = subClses.getFlattened();
        // System.out.println("Subclasses of vegetarian: ");
        // for (OWLClass cls : clses) {
        //    System.out.println("    " + cls);
        // }
        // System.out.println("\n");
        // In this case, we should find that the classes, cow, sheep and giraffe
        // are vegetarian. Note that in this ontology only the class cow had
        // been stated to be a subclass of vegetarian. The fact that sheep and
        // giraffe are subclasses of vegetarian was implicit in the ontology
        // (through other things we had said) and this illustrates why it is
        // important to use a reasoner for querying an ontology. We can easily
        // retrieve the instances of a class. In this example we'll obtain the
        // instances of the class pet. This class has a full IRI of
        // <http://owl.man.ac.uk/2005/07/sssw/people#pet> We need to obtain a
        // reference to this class so that we can ask the reasoner about it.

        // OWLClass country = fac.getOWLClass(IRI.create("http://owl.man.ac.uk/2005/07/sssw/people#pet"));
        // Ask the reasoner for the instances of pet
        //NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(country, true);
        // The reasoner returns a NodeSet again. This time the NodeSet contains
        // individuals. Again, we just want the individuals, so get a flattened
        // set.
        // Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        // System.out.println("Instances of pet: ");
        // for (OWLNamedIndividual ind : individuals) {
        //     System.out.println("    " + ind);
        // }
        // System.out.println("\n");
        // Again, it's worth noting that not all of the individuals that are
        // returned were explicitly stated to be pets. Finally, we can ask for
        // the property values (property assertions in OWL speak) for a given
        // individual and property. Let's get the property values for the
        // individual Mick, the full IRI of which is
        // <http://owl.man.ac.uk/2005/07/sssw/people#Mick> Get a reference to
        // the individual Mick
        // OWLNamedIndividual mick = fac.getOWLNamedIndividual(IRI.create("http://owl.man.ac.uk/2005/07/sssw/people#Mick"));
        // Let's get the pets of Mick Get hold of the has_pet property which has
        // a full IRI of <http://owl.man.ac.uk/2005/07/sssw/people#has_pet>

        // {ind: 'eva', prop: 'love'}
        OWLNamedIndividual eva = fac.getOWLNamedIndividual(IRI.create("http://www.semanticweb.org/sanny/ontologies/2018/1/untitled-ontology-10#Eva"));
        OWLObjectProperty love = fac.getOWLObjectProperty(IRI.create("http://www.semanticweb.org/sanny/ontologies/2018/1/untitled-ontology-10#love"));
        NodeSet<OWLNamedIndividual> petValuesNodeSet = reasoner.getObjectPropertyValues(eva, love);

        Set<OWLNamedIndividual> values = petValuesNodeSet.getFlattened();
        System.out.println("The love property values for Eva: ");
        for (OWLNamedIndividual ind : values) {
            System.out.println("    " + ind);
        }


        // Now ask the reasoner for the has_pet property values for Mick
        /*
        NodeSet<OWLNamedIndividual> petValuesNodeSet = reasoner.getObjectPropertyValues(mick, hasPet);
        Set<OWLNamedIndividual> values = petValuesNodeSet.getFlattened();
        System.out.println("The has_pet property values for Mick are: ");
        for (OWLNamedIndividual ind : values) {
            System.out.println("    " + ind);
        }
        */

        // Notice that Mick has a pet Rex, which wasn't asserted in the
        // ontology. Finally, let's print out the class hierarchy. Get hold of
        // the top node in the class hierarchy (containing owl:Thing) Now print
        // the hierarchy out

        /*
        Node<OWLClass> topNode = reasoner.getTopClassNode();
        print(topNode, reasoner, 0);
        */
    }

    private static void print(Node<OWLClass> parent, OWLReasoner reasoner, int depth) {
        // We don't want to print out the bottom node (containing owl:Nothing
        // and unsatisfiable classes) because this would appear as a leaf node
        // everywhere
        if (parent.isBottomNode()) {
            return;
        }
        // Print an indent to denote parent-child relationships
        printIndent(depth);
        // Now print the node (containing the child classes)
        printNode(parent);
        for (Node<OWLClass> child : reasoner.getSubClasses(
                parent.getRepresentativeElement(), true)) {
            // Recurse to do the children. Note that we don't have to worry
            // about cycles as there are non in the inferred class hierarchy
            // graph - a cycle gets collapsed into a single node since each
            // class in the cycle is equivalent.
            print(child, reasoner, depth + 1);
        }
    }

    private static void printIndent(int depth) {
        for (int i = 0; i < depth; i++) {
            System.out.print("    ");
        }
    }

    private static void printNode(Node<OWLClass> node) {
        DefaultPrefixManager pm = new DefaultPrefixManager(
                "http://owl.man.ac.uk/2005/07/sssw/people#");
        // Print out a node as a list of class names in curly brackets
        System.out.print("{");
        for (Iterator<OWLClass> it = node.getEntities().iterator(); it.hasNext();) {
            OWLClass cls = it.next();
            // User a prefix manager to provide a slightly nicer shorter name
            System.out.print(pm.getShortForm(cls));
            if (it.hasNext()) {
                System.out.print(" ");
            }
        }
        System.out.println("}");
    }
}