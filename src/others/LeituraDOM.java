/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package others;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import model.bean.Cidade;
import model.bean.Compra;
import model.bean.Estado;
import model.bean.PessoaJuridica;
import model.bean.Produto;
import model.bean.UnidadeMedida;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author hit
 */
public class LeituraDOM {
    
    Compra compra = new Compra();
    PessoaJuridica fornecedor = new PessoaJuridica();
    Produto produto = new Produto();
    Cidade cidade = new Cidade();
    Estado estado = new Estado();
    UnidadeMedida unid = new UnidadeMedida();
    
    List<Produto> listaProdutos = new ArrayList<>();
    
    public Compra getInfNota(String path, String tagName){
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder bld = fac.newDocumentBuilder();
            
            Document doc = bld.parse(path);
            
            NodeList listnodes = doc.getElementsByTagName(tagName);
            
            for(int i = 0; i < listnodes.getLength(); i++)
            {
                Node tag = listnodes.item(i);
                
                //Verifica se a tag e a da chave de acesso
                
                if(tagName == "infNFe")
                {
                    if(tag.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element elemTag = (Element) tag;
                        
                        compra.setChaveAcesso(elemTag.getAttribute("Id").substring(3));
                        compra = (Compra) getInfNota(path, "ide");
                    }
                }
                else
                {
                
                    if(tag.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element elements = (Element) tag;

                        NodeList listNodeChild = elements.getChildNodes();

                        for(int j = 0; j < listNodeChild.getLength(); j++)
                        {
                            Node tagChild = listNodeChild.item(j);

                            if(tagChild.getNodeType() == Node.ELEMENT_NODE)
                            {
                                Element elemTagChild = (Element) tagChild;

                                if(tagName.equals("ide"))
                                {
                                    switch(elemTagChild.getTagName())
                                    {
                                        case "dhEmi":
                                            compra.setDataCompra(Data.dataHoraParaAplicacao(elemTagChild.getTextContent().substring(0, 10) + " " + elemTagChild.getTextContent().substring(11, 19)));
                                            compra = (Compra) getInfNota(path, "ICMSTot");
                                            break;
                                    }
                                }
                                if(tagName.equals("ICMSTot"))
                                {
                                    switch(elemTagChild.getTagName())
                                    {
                                        case "vProd":
                                            compra.setPrecoTotal(Float.parseFloat(elemTagChild.getTextContent()));
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (ParserConfigurationException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                    Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            }
        return compra;
    }
    
    public PessoaJuridica getInfForn(String path, String tagName)
    {
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder bld = fac.newDocumentBuilder();
            
            Document doc = bld.parse(path);
            
            NodeList listTags = doc.getElementsByTagName(tagName);
            
            for(int i = 0; i < listTags.getLength(); i++)
            {
                Node tags = listTags.item(i);
                
                if(tags.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elemTag = (Element) tags;
                    
                    NodeList listChildTags = elemTag.getChildNodes();
                    
                    for(int j = 0; j < listChildTags.getLength(); j++)
                    {
                        Node tagChild = listChildTags.item(j);
                        
                        if(tagChild.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element listTagChilds = (Element) tagChild;
                            
                            if(tagName.equals("emit"))
                            {
                                switch(listTagChilds.getTagName())
                                    {
                                        case "CNPJ":
                                            String cnpj = listTagChilds.getTextContent();
                                            cnpj = cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12, 14);
                                            fornecedor.setCnpj(cnpj);
                                            break;
                                        case "xNome" :
                                            if(listTagChilds.getTextContent().length()>80)
                                                fornecedor.setRazaoSocial(listTagChilds.getTextContent().substring(0, 80));
                                            else fornecedor.setRazaoSocial(listTagChilds.getTextContent());
                                            break;
                                        case "xFant":
                                            if(listTagChilds.getTextContent().length()>80)
                                                fornecedor.setNomeFantasia(listTagChilds.getTextContent().substring(0, 80));
                                            else fornecedor.setNomeFantasia(listTagChilds.getTextContent());
                                            break;
                                        case "IE" :
                                            fornecedor.setIe(listTagChilds.getTextContent());
                                            fornecedor = (PessoaJuridica) getInfForn(path, "enderEmit");
                                            break;
                                    }
                                fornecedor.setObservacoes("");
                            }
                            if(tagName.equals("enderEmit"))
                            {
                                switch(listTagChilds.getTagName())
                                {
                                    case "xLgr":
                                        if(listTagChilds.getTextContent().length()>70)
                                            fornecedor.setLogradouro(listTagChilds.getTextContent().substring(0, 70));
                                        else fornecedor.setLogradouro(listTagChilds.getTextContent());
                                        break;
                                    case "nro":
                                        if(listTagChilds.getTextContent().length()>8)
                                            fornecedor.setNumero(listTagChilds.getTextContent().substring(0, 8));
                                        else fornecedor.setNumero(listTagChilds.getTextContent());
                                        break;
                                    case "xBairro":
                                        if(listTagChilds.getTextContent().length()>30)
                                            fornecedor.setBairro(listTagChilds.getTextContent().substring(0, 30));
                                        else fornecedor.setBairro(listTagChilds.getTextContent());
                                        break;  
                                    case "xMun":
                                        cidade.setCidade(listTagChilds.getTextContent());
                                        fornecedor.setCidade(cidade);
                                        break;
                                    case "UF" :
                                        estado.setUf(listTagChilds.getTextContent());
                                        cidade.setEstado(estado);
                                        fornecedor.setCidade(cidade);
                                        break;
                                    case "CEP" :
                                        fornecedor.setCep(listTagChilds.getTextContent());
                                        break;
                                    case "fone" :
                                        fornecedor.setTelefoneFixo(listTagChilds.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
            
        } catch (ParserConfigurationException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                    Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            }
        return fornecedor;
    }
    
    public List<Produto> getInfProd(String path, String tagName)
    {
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder bld = fac.newDocumentBuilder();
            
            Document doc = bld.parse(path);
            
            NodeList listaTags = doc.getElementsByTagName(tagName);
            
            for(int i = 0; i < listaTags.getLength(); i++)
            {
                Node tags = listaTags.item(i);
                
                if(tags.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elemTag = (Element) tags;
                    
                    NodeList listChildTags = elemTag.getChildNodes();
                    
                    for(int j = 0; j < listChildTags.getLength(); j++)
                    {
                        Node tagChild = listChildTags.item(j);
                        
                        if(tagChild.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element listTagChilds = (Element) tagChild;
                            
                            switch(listTagChilds.getTagName())
                            {
                                case "cProd" :
                                        if(listTagChilds.getTextContent().length()>25)
                                            produto.setCodigoFabricante(listTagChilds.getTextContent().substring(0, 25));
                                        else produto.setCodigoFabricante(listTagChilds.getTextContent());
                                        break;
                                    case "xProd" :
                                        if(listTagChilds.getTextContent().length()>50)
                                            produto.setDescricao(listTagChilds.getTextContent().substring(0, 50));
                                        else produto.setDescricao(listTagChilds.getTextContent());
                                        break;
                                    case "uCom" : 
                                        unid.setSigla(listTagChilds.getTextContent());
                                        produto.setUnidadeMedida(unid);
                                        break;
                                    case "qCom" : 
                                        String qtd = listTagChilds.getTextContent();
                                        String qtdConvertida = "";
                                            for(int k = 0; k<qtd.length();k++)
                                                if(qtd.charAt(k)!='.'){
                                                    qtdConvertida += qtd.charAt(k);
                                                } else break;
                                                
                                        produto.setQtdDisponivel(Integer.parseInt(qtdConvertida));
                                        break;
                                    case "vUnCom" :
                                        produto.setPrecoCompra(Float.parseFloat(listTagChilds.getTextContent()));
                                        produto.setPrecoVenda(Float.parseFloat(listTagChilds.getTextContent()));
                                        break;
                            }
                            produto.setCodigoOriginal("");
                            produto.setObservacoes("");
                        }
                    }
                     listaProdutos.add(produto);
                     produto = new Produto();
                }
            }
            
        } catch (ParserConfigurationException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                    Logger.getLogger(LeituraDOM.class.getName()).log(Level.SEVERE, null, ex);
            }
        return listaProdutos;
    }
    
}
