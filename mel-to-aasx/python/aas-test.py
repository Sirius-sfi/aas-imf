from aas import model  # Import all PYI40AAS classes from the model package

identifier = model.Identifier('https://acplt.org/Simple_Submodel', model.IdentifierType.IRI)
submodel = model.Submodel(identification=identifier)

# create a global reference to a semantic description of the property
semantic_reference = model.Reference(
    (model.Key(
        type_=model.KeyElements.GLOBAL_REFERENCE,
        local=False,
        value='http://acplt.org/Properties/SimpleProperty',
        id_type=model.KeyType.IRI
    ),)
)
property = model.Property(
    id_short='ExampleProperty',  # Identifying string of the element within the submodel namespace
    value_type=model.datatypes.String,  # Data type of the value
    value='exampleValue',  # Value of the property
    semantic_id=semantic_reference  # set the semantic reference
)
submodel.submodel_element.add(property)

from aas.adapter.xml import write_aas_xml_file

data: model.DictObjectStore[model.Identifiable] = model.DictObjectStore()
data.add(submodel)
#with open('Simple_Submodel.xml', 'w', encoding='utf-8') as f:
with open('Simple_Submodel.xml', 'wb') as f:
    write_aas_xml_file(file=f, data=data)
