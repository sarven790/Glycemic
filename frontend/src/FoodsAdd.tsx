import React, { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { Button, Form, Header, InputOnChangeData, Segment, Transition } from 'semantic-ui-react'
import SiteMenu from './components/SiteMenu'
import {ISingleFoods} from './models/IFood';
import { categories } from './Datas';
import { foodSave } from './Services';
import { useNavigate } from 'react-router-dom';
import { autControl } from './Util';

export default function FoodsAdd() {

  const navigate = useNavigate()

  // form item states
  const [name, setName] = useState("")
  const [glycemicindex, setGlycemicindex] = useState(0)
  const [source, setSource] = useState("")
  const [cid, setCid] = useState('0')
  const [base64Image, setBase64Image] = useState("")

   // animation
   const [visible, setVisible] = useState(false)

  // food fnc
  const fncSend = (e: React.FormEvent) => {
    e.preventDefault()
    toast.loading("Yükleniyor.")
    if(name === ""){
        toast.dismiss();
        toast.warning("Lütfen ürün adını giriniz!")
    }
    else if(glycemicindex === 0){
        toast.dismiss();
        toast.warning("Lütfen Glisemik indeksini giriniz!")
    }
    else if(cid === "0"){
        toast.dismiss();
        toast.warning("Lütfen kategori belirtiniz!")
    } 
    else if(base64Image === ""){
        toast.dismiss();
        toast.warning("Lütfen bir resim seçiniz!")
    }
    else if(source === ""){
        toast.dismiss();
        toast.warning("Lütfen kaynak giriniz!")
    }
    else{
        foodSave(parseInt(cid),name,glycemicindex,base64Image,source)
        .then(res => { 
            const food:ISingleFoods = res.data
            toast.dismiss(); 
            if ( food.status ) {
              // ekleme başarılı
              toast.success("Ürün ekleme işlemi başarılı")                   
            }else { 
              toast.error( food.message )
            }
           }).catch(err => {
            toast.dismiss();
            toast.error( "Ürün ekleme işlemi sırasında bir hata oluştu!" )
        })
    }
  }

  // image to base64
  const imageOnChange = (e:any, d:InputOnChangeData) => {
        const file = e.target.files[0]
        const size:number = file.size / 1024 // kb
        if ( size > 10 ) { // 10 kb
            toast.error("Lütfen max 10 kb bir resim seçiniz!")
        }else {
            getBase64(file).then( res => {
                setBase64Image(""+res)
            })
        } 
  }

  useEffect(() => {
    if( autControl() === null ) {
      localStorage.removeItem("user")
      localStorage.removeItem("aut")
      navigate("/")
    }
    setTimeout(() => {
      setVisible(true)
    }, 500);
  }, [])

  const getBase64 = ( file: any ) => {
    return new Promise(resolve => {
        let fileInfo;
        let baseURL:any = "";
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          baseURL = reader.result
          resolve(baseURL);
        };
        console.log(fileInfo);
      });
  }

  useEffect(() => {
    if( autControl() === null ) {
      localStorage.removeItem("user")
      localStorage.removeItem("aut")
      navigate("/")
    }
  }, [])


  return (
    <>
    <SiteMenu />
    <Header as='h3' block>
        Gıda Ekle
    </Header>
    <Transition visible={visible} animation='slide down' duration={750}>
      <Segment vertical color='grey'  >
        Burada eklediğiniz gıdalar, admin onayına gidip denetimden geçtikten sonra yayına alınır.
      </Segment>
    </Transition>

    <Form>
        <Form.Group widths='equal'>
          <Form.Input fluid label='Adı' onChange={(e) => setName(e.target.value)} placeholder='Adı' />
          <Form.Input type='number' min='0' max='150' onChange={(e) => setGlycemicindex(parseInt(e.target.value))} fluid label='Glisemik İndex' placeholder='Glisemik İndex' />
          <Form.Select  label='Kategori' value={cid} fluid placeholder='Kategori' options={categories} search onChange={(e,d) => setCid( ""+d.value )} />
        </Form.Group>
        
        <Form.Group widths='equal'>
            <Form.Input onChange={(e, d) => imageOnChange(e,d) } type='file' fluid label='Resim' placeholder='Resim' />
            <Form.Input fluid label='Kaynak' onChange={(e) => setSource(e.target.value)} placeholder='Kaynak' />
        </Form.Group>
        <Button onClick={(e) => fncSend(e) }>Gönder</Button>
      </Form>
    </>
  )
}